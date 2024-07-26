/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

/**
 *
 * @author K
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DailyLogger {
    private static final String LOG_DIR = "logs/"; // Directory where log files will be stored
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String FILE_EXTENSION = ".log";
    private final BlockingQueue<String> logQueue;
    private volatile boolean running;

    public DailyLogger() {
        this.logQueue = new LinkedBlockingQueue<>();
        this.running = true;
        startLoggingThread();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        this.log("Iniciando log");
    }

    // Method to add log entries to the queue
    public void log(String message) {
        if (running) {
            logQueue.offer(message);
        }
    }

    // Method to start the logging thread
    private void startLoggingThread() {
        Thread logThread = new Thread(() -> {
            while (running) {
                try {
                    String message = logQueue.take();
                    writeToFile(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    // Handle the exception (logging it, for example)
                    e.printStackTrace();
                }
            }
        });
        logThread.setDaemon(true);
        logThread.start();
    }

    // Method to write the message to the daily log file
    private void writeToFile(String message) throws IOException {
        String currentDate = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        String logFileName = LOG_DIR + currentDate + FILE_EXTENSION;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            writer.write(message);
            writer.newLine();
        }
    }

    // Method to stop the logger gracefully
    public void stop() {
        running = false;
    }

    public static void main(String[] args) {
        // Example usage
        DailyLogger logger = new DailyLogger();
        logger.log("This is a test log message.");
        logger.log("Another log message.");
        
        // To stop the logger when the application exits
        Runtime.getRuntime().addShutdownHook(new Thread(logger::stop));
    }
}
