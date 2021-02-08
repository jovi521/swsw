package com.cdyw.swsw.common.common.util;

import java.util.List;

/**
 * @author jovi
 */
public class MathUtil {

    /**
     * 查询数组中最接近的值
     *
     * @param value  值
     * @param values 集合
     * @return int
     */
    public static Double nearest(Double value, List<Double> values) {
        double min = Double.MAX_VALUE;
        Double closest = value;
        for (Double v : values) {
            final double diff = Math.abs(v - value);
            if (diff < min) {
                min = diff;
                closest = v;
            }
        }
        return closest;
    }

    /**
     * 查询数组中最接近的值
     *
     * @param value  值
     * @param values 集合
     * @return int
     */
    public static Long nearestLong(Long value, List<Long> values) {
        long min = Long.MAX_VALUE;
        Long closest = value;
        for (Long v : values) {
            final long diff = Math.abs(v - value);
            if (diff < min) {
                min = diff;
                closest = v;
            }
        }
        return closest;
    }
}
