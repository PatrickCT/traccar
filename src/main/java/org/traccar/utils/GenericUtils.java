/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author K
 */
public class GenericUtils {

    interface HasId {
        long getId();
    }
    private static final String[] WEEK_DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

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
}
