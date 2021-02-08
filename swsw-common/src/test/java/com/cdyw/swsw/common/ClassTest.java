package com.cdyw.swsw.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.txt.SatelliteFY4AParseTxt;
import com.cdyw.swsw.common.domain.entity.atstation.Atstation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@DisplayName("我的测试类")
public class ClassTest {

    @Test
    public void testClass() {
        SatelliteFY4AParseTxt entity = new SatelliteFY4AParseTxt();
        Atstation atstation = new Atstation();
        System.out.println(entity.toString());
    }

    @Test
    public void testString() {
        String str = "Z_RADA_I_56285_20201215000000_P_WPRD_LC_OOBS.TXT";
        String[] strSplits = str.split("_");
        for (String s : strSplits) {
            System.out.println(s.split("\\.")[0]);
        }
    }

    @Test
    public void testFile() {
        File file = new File("D:\\db\\swsw-files\\radarWeather_parse\\Z9280\\20200628\\CAPPI\\20200628_080400.00.008.000_4100_R3.png");
        String name = file.getName();
        String path = file.getPath();
        String[] splits = path.split("\\\\");
        System.out.println(name);
        System.out.println(path);
        for (String s : splits) {
            System.out.println(s);
        }
    }

    @Test
    public void testClassMap() {
        Map<String, Object> map = new HashMap<>(64);
        map.put("STATION_NAME", "111");
        Atstation atstation = BeanUtil.mapToBean(map, Atstation.class, true, CopyOptions.create());
        System.out.println(atstation);
    }

    @Test
    public void testDate() throws ParseException {
        long time = 1608626700;
        String patten = "yyyyMMddHHmmss";
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
            System.out.println(date);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testUtc2Local() throws ParseException {
        String source = "20201223162500";
        TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone timeZoneUtc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(timeZoneUtc);
        Date parse = sdf.parse(source);
        // 转化为local
        sdf.setTimeZone(timeZoneSH);
        Date parse1 = sdf.parse(source);
        System.out.println(parse);
        System.out.println(parse1);
    }

    @Test
    public void testDouble() {
        String d = "12.123456780987654345678";
        Double aDouble = new Double(d);
        Float f = new Float(d);
        BigDecimal bigDecimal = new BigDecimal(d);
        System.out.println(aDouble);
        System.out.println(bigDecimal);
        System.out.println(f);
    }

    public Double nearest(Double value, Double[] values) {
        double min = Double.MAX_VALUE;
        Double closest = value;
        int index = 0;
        for (int i = 0; i < values.length; i++) {
            Double v = values[i];
            final double diff = Math.abs(v - value);
            if (diff < min) {
                min = diff;
                closest = v;
                index = i;
            }
        }
        System.out.println("index***************: " + index);
        return closest;
    }

    @Test
    public void testNearest() {
        Double[] ds = {111.222, 222.222, 333.222};
        Double v = 222.111;
        Double nearest = nearest(v, ds);
        System.out.println(nearest);
    }

    @Test
    public void testTime() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        System.out.println(uuid);
        System.out.println(uuid);
    }

    @Test
    public void testStr() {
        String s = "2021年01月20日13时52分01秒";
        s = s.substring(8, 11);
        System.out.println(s);
    }

    @Test
    public void testFor() {
        int k = 0;
        for (int i = 0; i < 3; i++) {
            System.out.println("i = " + i);
            for (int j = 0; j < 8; j++) {
                System.out.println("j = " + j);
                while (k < 48) {
                    System.out.println("k = " + k);
                    if (k == 3) {
                        System.out.println("k = " + k);
                        k++;
                        break;
                    }
                    k++;
                }
            }
        }
    }

    @DisplayName("我的带参数日期测试")
    @ParameterizedTest
    @ValueSource(longs = {1611298800})
    @RepeatedTest(3)
    void testDate(long time) {
//        long time1 = 1611298800 * 1000L;
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String s = sdf.format(new Date(time * 1000L));
        System.out.println(s);
//        String f = DateUtil.format(new Date(time1),format);
////        String string = DateUtils.getDayStringByTimestamp(time1, format);
//        System.out.println(f);
    }

    @Test
    public void testTime1() {
        long time1 = 1611298800;
        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String s = sdf.format(new Date(time1 * 1000L));
        String string = DateUtils.getDayStringByTimestamp(time1, format);
        System.out.println(s);
        System.out.println(string);
    }

    @Test
    public void testFor1() {
        int j = 0;
        for (int i = 0; i < 10; i++) {
            System.out.println("i: " + i);
            while (j < 5) {
                System.out.println("j: " + j);
                if (i == j) {
                    j++;
                    break;
                }
                j++;
            }
        }
    }

    @Test
    public void testTimeChinese() {
        String timeChineseHours = DateUtils.getTimeChineseHours(1611295200);
        System.out.println(timeChineseHours);
    }
}
