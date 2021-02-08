package com.cdyw.swsw.system.vo.common;

import java.text.SimpleDateFormat;

public class TimeUtil {


    //根据时间戳获取字符串
    public static String getDayStringByTimestamp(long timestamp, String farmat) {
        SimpleDateFormat format = new SimpleDateFormat(farmat);
        long startDay = (timestamp * 1000);
        String startG = format.format(startDay);
        return startG;
    }

}
