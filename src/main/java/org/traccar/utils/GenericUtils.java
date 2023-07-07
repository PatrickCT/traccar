/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import java.util.Calendar;

/**
 *
 * @author K
 */
public class GenericUtils {

    private static final int SUNDAY = Calendar.SUNDAY;     // 0
    private static final int MONDAY = Calendar.MONDAY;     // 1
    private static final int TUESDAY = Calendar.TUESDAY;    // 2
    private static final int WEDNESDAY = Calendar.WEDNESDAY;  // 3
    private static final int THURSDAY = Calendar.THURSDAY;   // 4
    private static final int FRIDAY = Calendar.FRIDAY;     // 5
    private static final int SATURDAY = Calendar.SATURDAY;   // 6

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
}
