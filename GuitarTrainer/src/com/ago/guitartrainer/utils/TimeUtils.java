package com.ago.guitartrainer.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeUtils {

    /**
     * Calculates the "time ago" string for the <code>timeNow</code> in respect to <code>timePast</code>. In other
     * words, the "time ago" is calculated based on the difference (<code>timeNow</code> - <code>timePast</code>).
     * 
     * Examples for the differences and how they are formatted:
     * <ul>
     * <li>milliseconds are formatted like "123ms ago"
     * <li>seconds are formatted like 4" (precision in milliseconds is ignored)
     * <li>
     * <ul>
     * 
     * @param timePast
     * @param timeNow
     * 
     * */
    public static String formatTimeAgo(long timePast, long timeNow) {
        if (timePast >= timeNow)
            return "0";

        Calendar calTimePast = GregorianCalendar.getInstance();
        Calendar calTimeNow = GregorianCalendar.getInstance();

        calTimePast.setTimeInMillis(timePast);
        calTimeNow.setTimeInMillis(timeNow);

        int year = calcDiff(calTimePast, calTimeNow, Calendar.YEAR);
        int month = calcDiff(calTimePast, calTimeNow, Calendar.MONTH);
        int week = calcDiff(calTimePast, calTimeNow, Calendar.WEEK_OF_MONTH);
        int day = calcDiff(calTimePast, calTimeNow, Calendar.DAY_OF_YEAR);
        int hour = calcDiff(calTimePast, calTimeNow, Calendar.HOUR_OF_DAY);
        int minute = calcDiff(calTimePast, calTimeNow, Calendar.MINUTE);
        int second = calcDiff(calTimePast, calTimeNow, Calendar.SECOND);

        String str = "1s";
        if (year > 0) {
            str = year + "y" + month + "mo";
        } else if (month > 0) {
            int days = day % 30;
            str = month + "mo" + days + "d";
        } else if (week > 0) {
            int days = day % 7;
            str = week + "w" + days + "d";
        } else if (day > 0) {
            str = day + "d" + hour + "h";
        } else if (hour > 0) {
            str = hour + "h" + minute + "m";
        } else if (minute > 0) {
            str = minute + "m";
        } else if (second > 0) {
            str = second + "s";
        } else {
            str = "now";
        }

        return str;

    }

    public static String formatDuration(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        String str = "";

        if (hours > 0)
            str += hours + "h";
        if (minutes > 0 || str.length() > 0)
            str += " " + minutes + "m";
        if (seconds > 0 || str.length() > 0)
            str += " " + seconds + "s";
        return str;

    }

    private static int calcDiff(Calendar past, Calendar now, int field) {
        return now.get(field) - past.get(field);
    }
}
