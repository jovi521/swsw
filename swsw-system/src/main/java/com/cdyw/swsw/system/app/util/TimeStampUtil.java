package com.cdyw.swsw.system.app.util;

import cn.hutool.core.date.DateUtil;

import java.time.Instant;
import java.util.*;

/**
 * @author jovi
 */
public class TimeStampUtil {

    public static List<Map<String, String>> timeStampToLocalDateTime(String startTime, String endTime) {
        List<Map<String, String>> resultList = new ArrayList<>();
        Map<String, String> resultMap = new HashMap<>(16);
        Long startTimeL = Optional.ofNullable(startTime).map(Long::valueOf).orElse(null);
        Long endTimeL = Optional.ofNullable(endTime).map(Long::valueOf).orElse(null);
        String startTimeStr;
        String endTimeStr;
        if (startTimeL != null && endTimeL != null) {
            startTimeStr = DateUtil.formatLocalDateTime(DateUtil.toLocalDateTime(Instant.ofEpochSecond(startTimeL)));
            endTimeStr = DateUtil.formatLocalDateTime(DateUtil.toLocalDateTime(Instant.ofEpochSecond(endTimeL)));
            resultMap.put("startTimeStr", startTimeStr);
            resultMap.put("endTimeStr", endTimeStr);
            resultList.add(resultMap);
        }
        return resultList;
    }

    /**
     * 将毫秒的时间戳转化为格式化的时间字符串
     *
     * @param timeStamp String
     * @return String
     */
    public static String timeStampToLocalDateTimeStr(String timeStamp) {
        String result = null;
        Long timeStampL = Optional.ofNullable(timeStamp).map(Long::valueOf).orElse(null);
        if (timeStampL != null) {
            result = DateUtil.formatLocalDateTime(DateUtil.toLocalDateTime(Instant.ofEpochSecond(timeStampL)));
        }
        return result;
    }

    /**
     * 将毫秒的时间戳转化为yyyyMMdd格式化的时间字符串
     *
     * @param timeStamp String
     * @return String
     */
    public static String timeStampToYyyyMmDd(String timeStamp) {
        String result = null;
        Long timeStampL = Optional.ofNullable(timeStamp).map(Long::valueOf).orElse(null);
        if (timeStampL != null) {
            result = DateUtil.format(DateUtil.toLocalDateTime(Instant.ofEpochSecond(timeStampL)), "yyyyMMdd");
        }
        return result;
    }

    /**
     * 将毫秒的时间戳转化为yyyy格式化的时间字符串
     *
     * @param timeStamp String
     * @return String
     */
    public static String timeStampToYyyy(String timeStamp) {
        String result = null;
        Long timeStampL = Optional.ofNullable(timeStamp).map(Long::valueOf).orElse(null);
        if (timeStampL != null) {
            result = DateUtil.format(DateUtil.toLocalDateTime(Instant.ofEpochSecond(timeStampL)), "yyyy");
        }
        return result;
    }

    /**
     * 将毫秒的时间戳转化为指定格式的时间字符串
     *
     * @param timeStamp 时间戳
     * @param format    格式
     * @return String
     */
    public static String timeStampToTimeStr(Long timeStamp, String format) {
        String result = null;
        Long timeStampL = Optional.ofNullable(timeStamp).orElse(null);
        if (timeStampL != null) {
            result = DateUtil.format(DateUtil.toLocalDateTime(Instant.ofEpochSecond(timeStampL)), format);
        }
        return result;
    }

    /**
     * 将毫秒的时间戳转化为指定格式的本地时间字符串
     *
     * @param timeStamp 时间戳
     * @param format    格式
     * @return String
     */
    public static String timeStampToLocalTimeStr(Long timeStamp, String format) {
        String result = null;
        Long timeStampL = Optional.ofNullable(timeStamp).orElse(null);
        if (timeStampL != null) {
            result = DateUtil.format(DateUtil.toLocalDateTime(Instant.ofEpochSecond(timeStampL)).plusHours(8), format);
        }
        return result;
    }

    /**
     * 将毫秒的时间戳转化为指定格式的UTC时间字符串
     *
     * @param timeStamp 时间戳
     * @param format    格式
     * @return String
     */
    public static String timeStampToUtcTimeStr(Long timeStamp, String format) {
        String result = null;
        Long timeStampL = Optional.ofNullable(timeStamp).orElse(null);
        if (timeStampL != null) {
            result = DateUtil.format(DateUtil.toLocalDateTime(Instant.ofEpochSecond(timeStampL)).minusHours(8), format);
        }
        return result;
    }

}
