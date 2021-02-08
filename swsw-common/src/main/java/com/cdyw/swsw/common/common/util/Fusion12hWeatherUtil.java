package com.cdyw.swsw.common.common.util;

import cn.hutool.core.util.RandomUtil;
import com.cdyw.swsw.common.domain.ao.enums.Fusion12hWeatherStatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Fusion12h 天气元素的工具类（查对应云量、风）
 *
 * @author jovi
 */
public class Fusion12hWeatherUtil {

    /**
     * 根据云量数值查询相对应的字符
     *
     * @param typeValue 云量数值
     * @return 字符
     */
    public static String getCloudCharByValue(String typeValue) {
        if (typeValue == null) {
            // 暂时没云量，所以随时赋值
            List<Fusion12hWeatherStatusEnum> cloudValues = getWeatherAssignValues("CLOUD");
            return RandomUtil.randomEle(cloudValues).getWeatherName();
        } else {
            // 后期有值了继续判断
            return null;
        }
    }

    /**
     * 根据风的数值（包括风向和风速）查询对应的字符
     *
     * @param typeValue 风的数值
     * @return 字符
     */
    public static String getWindCharByValue(String typeValue) {
        if (typeValue == null) {
            List<Fusion12hWeatherStatusEnum> cloudValues = getWeatherAssignValues("WIND");
            return RandomUtil.randomEle(cloudValues).getWeatherName();
        } else {
            // 后期有值了继续判断
            return null;
        }
    }

    /**
     * 获取指定元素的枚举类
     *
     * @param assignEnumName 指定元素
     * @return list
     */
    private static List<Fusion12hWeatherStatusEnum> getWeatherAssignValues(String assignEnumName) {
        Fusion12hWeatherStatusEnum[] values = Fusion12hWeatherStatusEnum.values();
        List<Fusion12hWeatherStatusEnum> assignValues = new ArrayList<>(1);
        for (Fusion12hWeatherStatusEnum statusEnum : values) {
            if (statusEnum.name().contains(assignEnumName)) {
                assignValues.add(statusEnum);
            }
        }
        return assignValues;
    }
}
