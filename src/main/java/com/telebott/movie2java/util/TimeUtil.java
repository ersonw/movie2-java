package com.telebott.movie2java.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public static String getNowString(int v) {
        if (v > 13) {
            return null;
        }
        long time = System.currentTimeMillis();
        return timeToString(time, v);
    }

    public static String getNowString() {
        return getNowString(10);
    }
    public static String getNowDate() {
        LocalDateTime ld = LocalDateTime.now();
        return format(ld,"yyyy-MM-dd HH:mm:ss");
    }

    public static String timeToString(long time, int v) {
        String[] sTime = new String[]{Long.toString(time)};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < v; i++) {
            builder.append(sTime[i]);
        }
        return builder.toString();
    }

    public static String timeToString(long time) {
        return timeToString(time, 10);
    }

    public static long strToTime(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static long strToDateTime(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static long sevenDaysLater() {
        return manyDaysLater(7);
    }

    public static long sevenDaysBefore() {
        return manyDaysBefore(7);
    }

    public static long manyDaysLater(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days);
        return calendar.getTimeInMillis();
    }
    public static long dayToTime(String date){
        long time = (new SimpleDateFormat("yyyy-MM-dd")).parse(date, new ParsePosition(0)).getTime();
        if (time > 0) return time;
        return getTodayZero();
    }
    public static long manyDaysBefore(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - days);
        return calendar.getTimeInMillis();
    }
    public static long getDateZero(String str){
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return dayFormat.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  0L;
    }
    public static long getTodayZero(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    public static long getMonthZero(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    public static long getYearZero(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    public static long getAfterDaysZero(int days){
        return getTodayZero() + (days * 86400000L);
    }
    public static long getBeforeDaysZero(int days){
        return getTodayZero() - (days * 86400000L);
    }
    public static String format(Temporal co, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        String str = fmt.format(co);
//        System.out.println(pattern + ": " + str);
        return str;
    }
    public static String _getOrderNo(){
        LocalDateTime ld = LocalDateTime.now();
        return format(ld,"yyyyMMddHHmmssSSS");
    }
}
