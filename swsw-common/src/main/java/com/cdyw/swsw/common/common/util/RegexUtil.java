package com.cdyw.swsw.common.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 *
 * @author jovi
 */
public class RegexUtil {

    private static Pattern pattern;

    private static Matcher matcher;

    public static String getString(String regex, String[] strings) {
        String string = null;
        pattern = Pattern.compile(regex);
        for (String str : strings) {
            matcher = pattern.matcher(str);
            if (matcher.matches()) {
                string = str;
                break;
            }
        }
        return string;
    }

    public static String getString(String regex, String string) {
        String str = null;
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(string);
        if (matcher.matches()) {
            str = string;
        }
        return str;
    }
}
