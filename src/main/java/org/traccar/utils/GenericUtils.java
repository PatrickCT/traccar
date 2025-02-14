/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;
import org.threeten.bp.LocalTime;

/**
 *
 * @author K
 */
public class GenericUtils {

    interface HasId {

        long getId();
    }

    private static final String[] WEEK_DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int MONDAY = 1;     // 0000001
    private static final int TUESDAY = 2;    // 0000010
    private static final int WEDNESDAY = 4;  // 0000100
    private static final int THURSDAY = 8;   // 0001000
    private static final int FRIDAY = 16;    // 0010000
    private static final int SATURDAY = 32;  // 0100000
    private static final int SUNDAY = 64;    // 1000000

    public static List<String> convertBinaryToWeekDays(int binaryValue) {
        List<String> selectedDays = new ArrayList<>();

        for (int i = 0; i < WEEK_DAYS.length; i++) {
            int dayValue = (1 << i); // Calculate the binary value for the current day

            if ((binaryValue & dayValue) != 0) {
                selectedDays.add(WEEK_DAYS[i]);
            }
        }

        return selectedDays;
    }

    public static boolean isDaySelected(int selectedDays, int day) {

        switch (day) {
            case 64:
                return (selectedDays & SUNDAY) != 0;
            case 1:
                return (selectedDays & MONDAY) != 0;
            case 2:
                return (selectedDays & TUESDAY) != 0;
            case 4:
                return (selectedDays & WEDNESDAY) != 0;
            case 8:
                return (selectedDays & THURSDAY) != 0;
            case 16:
                return (selectedDays & FRIDAY) != 0;
            case 32:
                return (selectedDays & SATURDAY) != 0;
            default:
                return false;  // Invalid day provided
        }
    }

    public static <T extends HasId> String getIdsAsString(List<Long> strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        for (int i = 0; i < strings.size(); i++) {
            // Assuming 'id' is the variable name holding the ID value of each object
            sb.append(strings.get(i));

            if (i < strings.size() - 1) {
                sb.append(",");
            }
        }

        sb.append(")");

        return sb.toString();
    }

    public static Date addTimeToDate(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public static long aDate2Epoch(Date d) {
        return d.toInstant().toEpochMilli();
    }

    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        int year1 = cal1.get(Calendar.YEAR);
        int month1 = cal1.get(Calendar.MONTH);
        int day1 = cal1.get(Calendar.DAY_OF_MONTH);

        int year2 = cal2.get(Calendar.YEAR);
        int month2 = cal2.get(Calendar.MONTH);
        int day2 = cal2.get(Calendar.DAY_OF_MONTH);

        return (year1 == year2) && (month1 == month2) && (day1 == day2);
    }

    public static int getDayValue(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayValue = 0;

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                dayValue = MONDAY;
                break;
            case Calendar.TUESDAY:
                dayValue = TUESDAY;
                break;
            case Calendar.WEDNESDAY:
                dayValue = WEDNESDAY;
                break;
            case Calendar.THURSDAY:
                dayValue = THURSDAY;
                break;
            case Calendar.FRIDAY:
                dayValue = FRIDAY;
                break;
            case Calendar.SATURDAY:
                dayValue = SATURDAY;
                break;
            case Calendar.SUNDAY:
                dayValue = SUNDAY;
                break;
        }

        return dayValue;
    }

    public static Date parseTime(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.parse(time);
    }

    public static boolean checkElapsedTime(Date initial, Date last, int compareto) {
        ////System.out.println("ini " + initial.toString());
        ////System.out.println("last " + last.toString());
        LocalDateTime dateini = initial.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime datefin = last.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        Duration duration = Duration.between(datefin, dateini);
        ////System.out.println("duration " + duration.toMinutes());
        ////System.out.println(duration.toMinutes() >= compareto ? true : false);
        return duration.toMinutes() >= compareto ? true : false;
    }

    public static boolean checkIfBetween2Dates(Date origin, Date min, Date max) {
        Date first = new Date();
        first.setHours(min.getHours());
        first.setMinutes(min.getMinutes());
        Date last = new Date();
        last.setHours(max.getHours());
        last.setMinutes(max.getMinutes());

        Date original = new Date();
        original.setHours(origin.getHours());
        original.setMinutes(origin.getMinutes());

        return min.compareTo(origin) * origin.compareTo(max) >= 0;
        //return original.after(first) && original.before(last);
    }

    public static boolean isDateBetween(Date dateToCheck, Date startDate, Date endDate) {
        return dateToCheck.after(startDate) && dateToCheck.before(endDate);
    }

    public static boolean isTimeInRange(int[] current, int[] start, int[] end) {
        LocalTime startRange = LocalTime.of(start[0], start[1]);
        LocalTime endRange = LocalTime.of(end[0], end[1]);
        LocalTime timeToCheck = LocalTime.of(current[0], current[1]);

        System.out.println(startRange);
        System.out.println(endRange);
        System.out.println(timeToCheck);
        if (endRange.isBefore(startRange)) {
            // If the end time is before the start time, check if the time to check is after the start time
            // or before the end time (i.e., it's between midnight and the end of the range)
            return timeToCheck.isAfter(startRange) || timeToCheck.isBefore(endRange);
        } else {
            // Normal case where the end time is after the start time
            return timeToCheck.isAfter(startRange) && timeToCheck.isBefore(endRange);
        }
//        if (endRange.isBefore(startRange)) {
//            endRange.plusHours(24);
//        }
//        return timeToCheck.isAfter(startRange) && timeToCheck.isBefore(endRange);
    }

    public static int[] fetchUTCDate() throws IOException, ParseException {
        int[] result = new int[2];
        Date utcFallBack = dateToUTC(new Date());
        result[0] = utcFallBack.getHours();
        result[1] = utcFallBack.getMinutes();

        return result;

    }

    public static int[] fetchUTCDateWithRetry() throws IOException, ParseException {
        int maxRetries = 3;
        int[] result = new int[2];
        Date utcFallBack = dateToUTC(new Date());
        result[0] = utcFallBack.getHours();
        result[1] = utcFallBack.getMinutes();

        for (int retry = 0; retry < maxRetries; retry++) {
            try {
                URL url = new URL("https://worldtimeapi.org/api/timezone/utc");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());

                String datetime = json.getString("datetime");
                String time = datetime.split("T")[1];
                result[0] = Integer.parseInt(time.split(":")[0]);
                result[1] = Integer.parseInt(time.split(":")[1]);

                return result;
            } catch (Exception e) {
                // Print the exception for the current retry
                e.printStackTrace();

                // If not the last retry, wait before the next retry
                if (retry < maxRetries - 1) {
                    try {
                        Thread.sleep(1000); // Wait for 1 second before retry
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }

        // If all retries fail, return the default result
        return result;
    }

    public static Date localDate2UTC() throws ParseException {
        // Create a Date object representing the local time
        Date localDate = new Date();

        // Define the time zone for UTC
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

        // Get the time zone offset
        int timeZoneOffset = utcTimeZone.getOffset(localDate.getTime());

        // Adjust the time by subtracting the offset
        long utcTime = localDate.getTime() - timeZoneOffset;

        // Create a new Date object with the adjusted time
        Date utcDate = new Date(utcTime);

        return utcDate;
    }

    public static Date dateToUTC(Date date) {
        System.out.println(new Date() + " to UTC");
        return new Date(date.getTime() - Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }

    public static Date utcToDate(Date utcDate) {
        return new Date(utcDate.getTime() + Calendar.getInstance().getTimeZone().getOffset(utcDate.getTime()));
    }

    public static String genericPOST(String url, String json, Map<String, Object> headers) throws IOException {
        //
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            if (headers.isEmpty()) {
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
            } else {
                headers.forEach((a, b) -> {
                    httpPost.addHeader(a, b);
                });
            }

            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");

            client.close();

            return responseString;
        } catch (org.apache.hc.core5.http.ParseException ex) {
            //
            //System.out.println(ex.getMessage());
            //
        }
        return "";
    }

    public static String genericPUT(String url, String json, Map<String, Object> headers) throws IOException {
        //
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPut httpPost = new HttpPut(url);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            if (headers.isEmpty()) {
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
            } else {
                headers.forEach((a, b) -> {
                    httpPost.addHeader(a, b);
                });
            }

            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");

            client.close();

            return responseString;
        } catch (org.apache.hc.core5.http.ParseException ex) {
            //
            //System.out.println(ex.getMessage());
            //
        }
        return "";
    }

    public HttpEntity genericPOSTResponse(String url, String json) throws IOException {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");
            //
            client.close();

            return responseEntity;
        } catch (org.apache.hc.core5.http.ParseException ex) {
        }
        return null;
    }

    public byte[] genericPOSTResponseBytes(String url, String json) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        byte[] res = IOUtils.toByteArray(responseEntity.getContent());
        client.close();
        return res;

    }

    public static List<Date> getDatesBetween(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (calendar.getTime().before(endDate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        return dates;
    }

    public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {

        long diffInMillies = date2.getTime() - date1.getTime();

        //create the list
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);

        //create the result map of TimeUnit and difference
        Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
        long milliesRest = diffInMillies;

        for ( TimeUnit unit : units ) {

            //calculate difference in millisecond
            long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;

            //put the result in the map
            result.put(unit,diff);
        }

        return result;
    }

    public static String getLocalIP() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String hostAddress = localhost.getHostAddress();
            return GenericUtils.findServerName(hostAddress);
        } catch (UnknownHostException e) {
            System.err.println("Unable to get the host IP address: " + e.getMessage());
        }
        return "";
    }

    public static String findServerName(String ip) {
        Map<String, String> servers = new HashMap<>();
        servers.put("173.255.203.21", "Alba");
        servers.put("198.58.114.237", "AutoKapital");
        servers.put("45.56.69.178", "Cancun");
        servers.put("45.33.23.6", "Cemex");
        servers.put("69.164.203.19", "Merida");
        servers.put("45.56.79.216", "Order");
        servers.put("23.239.29.228", "Evolucion");
        servers.put("45.56.126.173", "Rastreo");
        servers.put("45.79.45.108", "Pruebas");
        servers.put("23.239.25.71", "Transporte");
        servers.put("45.56.66.144", "Comit");
        servers.put("45.33.116.80", "Combis");

        return servers.getOrDefault(ip, "development");
    }

    public static <T> void printArray(T[] array) {
        System.out.println("/[");

        for (T element : array) {
            System.out.print(element + ", ");
        }
        System.out.println("/]");
        System.out.println(); // Move to the next line after printing the array
    }

    public static <T> String printArray(T[] array, boolean return_value) {
        if (!return_value) {
            printArray(array);
            return "";
        }
        return String.format("[\n\r%s\n\r]", StringUtils.join(array, ", "));
    }

    public static String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(ALPHANUMERIC.length());
            char randomChar = ALPHANUMERIC.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
