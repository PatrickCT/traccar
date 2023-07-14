/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.traccar.model.BaseModel;

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
            case 0:
                return (selectedDays & SUNDAY) != 0;
            case 1:
                return (selectedDays & MONDAY) != 0;
            case 2:
                return (selectedDays & TUESDAY) != 0;
            case 3:
                return (selectedDays & WEDNESDAY) != 0;
            case 4:
                return (selectedDays & THURSDAY) != 0;
            case 5:
                return (selectedDays & FRIDAY) != 0;
            case 6:
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
}
