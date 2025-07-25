/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

/**
 *
 * @author K
 */

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DeviceDailyLogger {

    private static final String LOG_DIR = "logs/";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String FILE_EXTENSION = ".log";
    private static final int RETENTION_DAYS = 7;
    private static final int MAX_FILES_PER_DEVICE = 10;

    private final BlockingQueue<LogEntry> logQueue;
    private final Map<String, BufferedWriter> writerCache;
    private final SimpleDateFormat dateFormat;
    private volatile boolean running;
    private String currentDate;

    private static class LogEntry {
        String deviceId;
        String message;
        String date;

        LogEntry(String deviceId, String message, String date) {
            this.deviceId = deviceId;
            this.message = message;
            this.date = date;
        }
    }

    public DeviceDailyLogger() {
        this.logQueue = new LinkedBlockingQueue<>();
        this.writerCache = new ConcurrentHashMap<>();
        this.dateFormat = new SimpleDateFormat(DATE_FORMAT);
        this.currentDate = dateFormat.format(new Date());
        this.running = true;

        startLoggingThread();
        startCleanupThread();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        log("System", "Iniciando log");
    }

    public void log(long deviceId, String message) {
        if (running) {
            String date = dateFormat.format(new Date());
            logQueue.offer(new LogEntry(String.valueOf(deviceId), "[" + System.currentTimeMillis() + "] " + message, date));
        }
    }

    public void log(String deviceId, String message) {
        if (running) {
            String date = dateFormat.format(new Date());
            logQueue.offer(new LogEntry(deviceId, "[" + System.currentTimeMillis() + "] " + message, date));
        }
    }

    private void startLoggingThread() {
        Thread logThread = new Thread(() -> {
            while (running || !logQueue.isEmpty()) {
                try {
                    LogEntry entry = logQueue.poll(1, TimeUnit.SECONDS);
                    if (entry != null) {
                        rotateIfNeeded();
                        writeToFile(entry);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            closeAllWriters();
        });
        logThread.setDaemon(true);
        logThread.start();
    }

    private synchronized void rotateIfNeeded() {
        String today = dateFormat.format(new Date());
        if (!today.equals(currentDate)) {
            closeAllWriters();
            currentDate = today;
        }
    }

    private void writeToFile(LogEntry entry) throws IOException {
        String fileName = LOG_DIR + entry.deviceId + "_" + entry.date + FILE_EXTENSION;

        BufferedWriter writer = writerCache.computeIfAbsent(fileName, path -> {
            try {
                return new BufferedWriter(new FileWriter(path, true));
            } catch (IOException e) {
                throw new RuntimeException("Error creating writer for: " + path, e);
            }
        });

        synchronized (writer) {
            writer.write(entry.message);
            writer.newLine();
            writer.flush(); // Consider using a batch flush if very high volume
        }
    }

    private void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (running) {
                try {
                    cleanupOldLogs();
                    Thread.sleep(TimeUnit.HOURS.toMillis(6)); // Run cleanup every 6 hours
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    private void cleanupOldLogs() {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists() || !logDir.isDirectory()) return;

        File[] files = logDir.listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
        if (files == null) return;

        long cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(RETENTION_DAYS);

        Map<String, List<File>> filesByDevice = new HashMap<>();

        for (File file : files) {
            if (file.lastModified() < cutoffTime) {
                file.delete();
                continue;
            }

            // Group by deviceId (e.g., device123_2024-07-24.log ? device123)
            String name = file.getName();
            int underscoreIndex = name.indexOf('_');
            if (underscoreIndex > 0) {
                String deviceId = name.substring(0, underscoreIndex);
                filesByDevice.computeIfAbsent(deviceId, k -> new ArrayList<>()).add(file);
            }
        }

        for (Map.Entry<String, List<File>> entry : filesByDevice.entrySet()) {
            List<File> logs = entry.getValue();
            if (logs.size() > MAX_FILES_PER_DEVICE) {
                logs.sort(Comparator.comparingLong(File::lastModified));
                for (int i = 0; i < logs.size() - MAX_FILES_PER_DEVICE; i++) {
                    logs.get(i).delete();
                }
            }
        }
    }

    private void closeAllWriters() {
        for (BufferedWriter writer : writerCache.values()) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writerCache.clear();
    }

    public void stop() {
        running = false;
    }
}
