package com.cdyw.swsw.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cdyw.swsw.common.domain.ao.txt.CommonRadarParseTxt;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTest {

    @Test
    public void testPatten() {
        String str = "D:\\data\\相控阵\\xindu\\20201210\\Z_RADR_I_ZCD02_20201210000006_O_DOR_DXK_CAR.bin.bz2";
        String[] split = str.split("\\\\");
        System.out.println(str);
        String pattern = "^\\S+\\d{4}\\d{2}\\d{2}\\S+$";
        String pattern2 = "^\\d{14}$";
        for (String s : split) {
            Pattern r = Pattern.compile(pattern2);
            Matcher m = r.matcher(str);
        }
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        System.out.println(m.matches());
    }

    @Test
    public void testPatten2() {
        String str = "Z_RADR_I_ZCD02_20201210000006_O_DOR_DXK_CAR.bin.bz2";
        String[] split = str.split("_");
        String pattern = "^\\d{14}$";
        String date = null;
        for (String s : split) {
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(s);
            if (m.matches()) {
                date = s;
                break;
            }
        }
        if (date != null) {
            LocalDateTime parse = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }
    }

    @Test
    public void testString() {
        String s = "D:\\Data\\相控阵\\xindu\\20201210\\Z_RADR_I_ZCD02_20201210000006_O_DOR_DXK_CAR.bin.bz2";
        System.out.println(s.contains("/tianfu/"));
    }

    @Test
    public void testFilePath() {
        String fileSource = "D:\\Data\\相控阵\\xindu\\20201210\\";
        File file = new File(fileSource);
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getParent());
        System.out.println(file.getName());
        System.out.println(file.getPath());
        String fileDestPath = "D:\\Data";
        File destFile = FileUtil.copy(fileSource, fileDestPath, false);
        System.out.println(destFile);
    }

    @Test
    public void testRegex() {
        String content = "Z_RADR_I_ZCD02_20201210000006_O_DOR_DXK_CAR.bin.bz2";
        String[] contentSplits = content.split("_");
        String regex = "^\\d{12,14}$";
        String regexSta = "^\\w{3}\\d{2}$";
        Pattern pattern = Pattern.compile(regex);
        for (String s1 : contentSplits) {
            String s = ReUtil.get(regex, s1, 0);
            System.out.println(s);
        }
    }

    @Test
    public void testDate() {
        String parse = LocalDateTime.parse("20201210000006", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        System.out.println(parse);
    }

    @Test
    public void testFilePath1() throws IOException {
        File file = new File("D:\\Data\\333.json");
//        File fileDestPath = new File("D:\\Data\\");
//        Path path = Paths.get("D:\\Data\\111.png");
//        Path target = Paths.get("D:\\Data\\222.png");
//        Files.copy(path,target);
        JSONObject jsonObject = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
        JSONArray aa = jsonObject.getJSONArray("aa");
        Object o = aa.get(0);
        JSONObject json = jsonObject.putOpt(o.toString(), "777");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(jsonObject.toStringPretty());
        bw.flush();
        bw.close();
//        FileUtils.copyFile(fileDestPath, fileDestPath);
//        FileUtil.copyFile(file, fileDestPath, StandardCopyOption.REPLACE_EXISTING);
//        File copy = new File(fileDestPath.getPath() + "\\" + file.getName() + "sss");
//        System.out.println(copy);
    }

    @Test
    public void testFilePath2() {
        String pathName = "D:\\ParseDataFiles\\天气雷达\\Z9280\\20210107\\EB\\20210107_094900.00.007.000_18.5_R2.PNG";
        Path path = Paths.get(pathName);
        File file = new File(pathName);
        String prefix = FileUtil.getPrefix(file);
        String suffix = FileUtil.getSuffix(file);
        File parent = FileUtil.getParent(file, 2);
        System.out.println(file.getPath());
        System.out.println(file.toPath());
        int nameCount = path.getNameCount();
        System.out.println(nameCount);
        Path lastPathEle = FileUtil.getPathEle(path, nameCount - 2);
        System.out.println(prefix);
        System.out.println(suffix);
        System.out.println(parent);
        System.out.println(lastPathEle);
    }

    @Test
    public void testFileCopy() throws IOException {
        File file = new File("D:\\Data\\333.json");
        File dest = new File("D:\\Data\\相控阵\\");
        File copy1 = FileUtil.copy(file, dest, false);
        System.out.println(copy1);
        Path path = Paths.get("D:\\Data\\111.png");
        Path target = Paths.get("D:\\Data\\相控阵\\444.png");
        Path copy = Files.copy(path, target);
        System.out.println(copy);
    }

    @Test
    public void testFileParse() throws IOException {
        String pathName = "D:\\db\\swsw-files\\radarWeather_parse\\Z9280\\20210107\\EB\\20210107_094900.00.007.000_18.5_R2_PNG.txt";
        File file = new File(pathName);
        List<String> strings = FileUtil.readLines(file, StandardCharsets.UTF_8);
        strings.removeIf(String::isEmpty);
        strings.removeIf(s -> s.contains("<"));
        Map<String, Object> values = new HashMap<>(14);
        for (String s : strings) {
            values.put(StrUtil.lowerFirst(s.substring(0, s.indexOf(":"))), s.substring(s.indexOf(":") + 1));
        }
        CommonRadarParseTxt radarParseTxt = BeanUtil.mapToBean(values, CommonRadarParseTxt.class, true, CopyOptions.create());
        System.out.println(radarParseTxt);
    }

    @Test
    public void testJol() {
        String weather = "aaa";
        ClassLayout classLayout = ClassLayout.parseInstance(weather);
        System.out.println("天气情况对象内部信息： " + classLayout.toPrintable());
        System.out.println("天气情况对象内部大小： " + classLayout.instanceSize());
        GraphLayout graphLayout = GraphLayout.parseInstance(weather);
        System.out.println("天气情况对象外部信息： " + graphLayout.toPrintable());
        System.out.println("天气情况对象外部大小： " + graphLayout.totalSize());
    }
}
