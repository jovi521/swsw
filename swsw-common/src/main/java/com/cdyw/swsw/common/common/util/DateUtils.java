package com.cdyw.swsw.common.common.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/*
 * @author jovi
 */
public class DateUtils {


    private static SimpleDateFormat sf = null;

    /*获取系统时间 格式为："yyyy/MM/dd "*/
    public static String getCurrentDate() {
        Date d = new Date();
        sf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sf.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sf.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString2(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyyMMddHHmm");
        return sf.format(d);
    }

    /*将字符串转为时间戳,得到的值为毫秒值*/
    public static long getStringToDate(String time) {
        sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /*将字符串转为时间戳,得到的值为毫秒值*/
    public static String getLocalString(String time) {
        LocalDateTime yyyyMMddHHmmss = LocalDateTimeUtil.parse(time, "yyyyMMddHHmmss");
        return yyyyMMddHHmmss.plusHours(8).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /*将字符串转为时间戳,得到的值为毫秒值*/
    public static long getStringToDate2(String time) {
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /*将字符串转为时间戳,得到的值为毫秒值*/
    public static long getStringToDate3(String time) {
        sf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /*将字符串转为时间戳,得到的值为毫秒值*/
    public static long getStringToDate4(String time) {
        sf = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /*将字符串转为时间戳,得到的值为毫秒值*/
    public static long getStringToDate5(String time) {
        sf = new SimpleDateFormat("yyyyMMddHH");
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 获取指定日期当月的第一天
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static String getFirstDayOfGivenMonth(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, 0);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  * 获取指定日期下个月的第一天
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static String getFirstDayOfNextMonth(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, 1);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  * 获取指定日期的下一天
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static String getNextDay(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一个月的最后一天
     */
    public static String getLastDay(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int lastDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date time = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, lastDays);
//        time.setDate(lastDays);
        return df.format(time);
    }


    //根据时间戳获取字符串
    public static String getDayStringByTimestamp(long timestamp, String farmat) {
        SimpleDateFormat format = new SimpleDateFormat(farmat);
        long startDay = (timestamp * 1000);
        return format.format(startDay);
    }

    /**
     * 将国际时间转化为本地时间戳(秒)
     *
     * @param dateUtc UTC时间字符串
     * @return 本地时间戳
     */
    public static Long getDateUtc2TimeLocal(String dateUtc, String patten) {
        SimpleDateFormat sdf = new SimpleDateFormat(patten);
        long time = 0;
        // 指定源时间为 UTC 时间
        sdf.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        try {
            Date parse = sdf.parse(dateUtc);
            // 再次指定为本地时间再进行时间戳转化
            sdf.setTimeZone(TimeZone.getDefault());
            time = (parse.getTime()) / 1000;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return time;
    }

    /**
     * 将国际时间转化为本地时间
     *
     * @param dateUtc UTC时间字符串
     * @return 本地时间
     */
    public static LocalDateTime getDateUtc2Local(String dateUtc, String patten) {
        LocalDateTime date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(patten);
        long time = 0;
        // 指定源时间为 UTC 时间
        sdf.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        try {
            Date parse = sdf.parse(dateUtc);
            // 再次指定为本地时间再进行时间戳转化
            sdf.setTimeZone(TimeZone.getDefault());
            time = (parse.getTime()) / 1000;
            date = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofHours(8));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    /**
     * 将国际时间戳转化为本地时间戳
     *
     * @param time UTC时间字符串
     * @return 本地时间
     */
    public static LocalDateTime getTimeUtc2Local(Long time, String patten) {
        TimeZone timeZoneSh = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone timeZoneUtc = TimeZone.getTimeZone("UTC");
        Date source = new Date(time * 1000);
        LocalDateTime date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(patten);
            // 指定源时间为 UTC 时间
            sdf.setTimeZone(timeZoneUtc);
            Date parse = sdf.parse(sdf.format(source));
            // 再次指定为本地时间再进行时间戳转化
            sdf.setTimeZone(timeZoneSh);
            time = (parse.getTime()) / 1000;
            date = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofHours(8));
            date = date.plusHours(8);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static LocalDateTime getUtc2Local(Long time) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofHours(8));
        return dateTime.plusHours(8);
    }

    public static LocalDateTime getLocalDateTime(Long time) {
        return LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.of("+8"));
    }

    /**
     * 根据秒时间戳获取带中文”年月日时分“的时间
     *
     * @param timestamp 秒时间戳
     * @return ”年月日时分“的时间
     */
    public static String getTimeChineseMinute(long timestamp) {
        LocalDateTime timeLocal = DateUtils.getLocalDateTime(timestamp);
        Date timeFrom = Date.from(timeLocal.toInstant(ZoneOffset.of("+8")));
        String timeFormat = DateUtil.formatChineseDate(timeFrom, false, true);
        timeFormat = StrUtil.subBefore(timeFormat, "分", false) + "分";
        return timeFormat;
    }

    /**
     * 根据秒时间戳获取带中文”月日时“的时间
     *
     * @param timestamp 秒时间戳
     * @return ”年月日时分“的时间
     */
    public static String getTimeChineseHours(long timestamp) {
        LocalDateTime timeLocal = DateUtils.getLocalDateTime(timestamp);
        Date timeFrom = Date.from(timeLocal.toInstant(ZoneOffset.of("+8")));
        String timeFormat = DateUtil.formatChineseDate(timeFrom, false, true);
        timeFormat = StrUtil.subBetween(timeFormat, "年", "时") + "时";
        return timeFormat;
    }

    /**
     * 根据秒时间戳获取加时间后的时间
     *
     * @param timestamp 秒时间戳
     * @return ”年月日时分“的时间
     */
    public static long getTimePlus(long timestamp, int plus) {
        LocalDateTime plusHours = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(8)).plusHours(plus);
        return plusHours.toEpochSecond(ZoneOffset.ofHours(8));
    }

    /**
     * 比较两个时间是否是同一天
     *
     * @param before 前一个时间
     * @param after  后一个时间
     * @return Boolean
     */
    public static boolean isSameDay(long before, long after) {
        LocalDateTime beforeLocal = DateUtils.getLocalDateTime(before);
        Date beforeFrom = Date.from(beforeLocal.toInstant(ZoneOffset.of("+8")));
        LocalDateTime afterLocal = DateUtils.getLocalDateTime(after);
        Date afterFrom = Date.from(afterLocal.toInstant(ZoneOffset.of("+8")));
        return DateUtil.isSameDay(beforeFrom, afterFrom);
    }

    /**
     * 获得偏移指定月数之后的时间
     * @param time
     * @param monthCount
     * @return
     */
    public static String offsetMonths(String time, int monthCount){
        Date date = DateUtil.parse(time);
        Date newDate = DateUtil.offset(date, DateField.MONTH, monthCount);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatTime = sdf.format(newDate);
        return formatTime;
    }
}
