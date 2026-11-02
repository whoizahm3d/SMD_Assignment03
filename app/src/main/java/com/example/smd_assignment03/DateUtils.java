package com.example.smd_assignment03;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static long startOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long startOfDay(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static boolean isToday(long millis) {
        if (millis <= 0)
        {
            return false;
        }
        return startOfDay(millis) == startOfToday();
    }

    public static boolean isBeforeToday(long millis) {
        if (millis <= 0)
        {
            return false;
        }
        return startOfDay(millis) < startOfToday();
    }

    public static String format(long millis) {
        if (millis <= 0)
        {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
