//package com.cdyw.swsw;
//
//import cn.hutool.core.date.DateTime;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.io.StreamProgress;
//import cn.hutool.core.lang.Console;
//import cn.hutool.http.HttpUtil;
//import cn.hutool.json.JSON;
//import cn.hutool.json.JSONUtil;
//import com.cdyw.swsw.data.domain.dao.atstation.AtstationMapper;
//import com.cdyw.swsw.domain.dao.atstation.AtstationMapper;
//import com.cdyw.swsw.domain.dao.colorvalue.ColorValueMapper;
//import io.swagger.models.auth.In;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.*;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.*;
//
//@SpringBootTest
//class SwswDataApplicationTests {
//
//    @Autowired
//    private AtstationMapper atstationMapper;
//    @Autowired
//    private ColorValueMapper colorValueMapper;
//
//
//    @Test
//    void contextLoads() {
//        /*测试合并两个类型相同的list*/
//        List<String> list1 = new ArrayList<>();
//        List<String> list2 = new ArrayList<>();
//        //给list1赋值
//        list1.add("1");
//        list1.add("2");
//        list1.add("3");
//        list1.add("4");
//        //给list2赋值
//        list2.add("1");
//        list2.add("2");
//        list2.add("3");
//        list2.add("4");
//        //将list1.list2合并
//        list2.removeAll(list1);
//        list2.addAll(list1);
//        //循环输出list1 看看结果
//        list1.forEach(System.out::println);
//        System.out.println("**************************");
//        list2.forEach(System.out::println);
//    }
//
//    @Test
//    void testFileOfHutool() {
//        String fileThirdUrl = "http://10.194.130.40:8888/WRSS/data/ftpfile.svt?url=L0NNQURBQVMvREFUQS9TQVRFL0ZZLTJHL1NBVEVfTDJfRjJHX1ZJU1NSX01XQl9MQlRfU0VDX0xDTi8yMDIwLzIwMjAwMTA1L1pfU0FURV9DX0JBV1hfMjAyMDAxMDUwMzIwMDRfUF9GWTJHX1NFQ19JUjJfTENOXzIwMjAwMTA1XzAzMDAuQVdY";
////        long size = HttpUtil.downloadFile(fileThirdUrl, FileUtil.file("E:\\tempDownloads"));
//        long size = HttpUtil.downloadFile(fileThirdUrl, FileUtil.file("E:/tempDownloads111/"), new StreamProgress() {
//
//            @Override
//            public void start() {
//                Console.log("开始下载。。。。");
//            }
//
//            @Override
//            public void progress(long progressSize) {
//                Console.log("已下载：{}", FileUtil.readableFileSize(progressSize));
//            }
//
//            @Override
//            public void finish() {
//                Console.log("下载完成！");
//            }
//        });
//        System.out.println("文件大小为： " + size);
//    }
//
//    @Test
//    void testFileOfHutool1() {
//        String fileThirdUrl = "http://10.194.130.40:8888/WRSS/data/ftpfile.svt?url=L0NNQURBQVMvREFUQS9TQVRFL0ZZLTJHL1NBVEVfTDJfRjJHX1ZJU1NSX01XQl9MQlRfU0VDX0xDTi8yMDIwLzIwMjAwMTA1L1pfU0FURV9DX0JBV1hfMjAyMDAxMDUwMzIwMDRfUF9GWTJHX1NFQ19JUjJfTENOXzIwMjAwMTA1XzAzMDAuQVdY";
//        if (FileUtil.exist("E:/tempDownloads222/20200605/111")) {
//            long size = HttpUtil.downloadFile(fileThirdUrl, FileUtil.file("E:/tempDownloads222/20200605/111"));
//            System.out.println("文件大小为： " + size);
//        } else {
//            long size = HttpUtil.downloadFile(fileThirdUrl, FileUtil.mkdir("E:/tempDownloads222/20200605/111"));
//            System.out.println("文件大小为： " + size);
//        }
//    }
//
//    @Test
//    void testDateFormatOfHutool() {
//        String now = DateUtil.format(LocalDateTime.now(ZoneId.of("UTC")), "yyyy-MM-dd HH:mm:ss");
//        System.out.println(now);
//    }
//
//
//    @Test
//    public void queryAtstation(){
//        String tableName = "data_atstation_202007";
//        String datetime = "2020-07-01 21:00:00";
//        String fieldStr1 = "max(lon),min(lon),max(lat),min(lat)";
//        String fieldStr2 = "lon,lat,tem";
//        Float filterStr = 100.0f;
//        String typeStr = "tem";
//        List<HashMap<String, Object>> lists1 = atstationMapper.getByDatetime(tableName, datetime, fieldStr1,typeStr,filterStr);
//        List<HashMap<String, Object>> lists2 = atstationMapper.getByDatetime(tableName, datetime, fieldStr2,typeStr,filterStr);
//        System.out.println("list2:" + lists2.size());
//        List<HashMap<String, Object>> colorLists = colorValueMapper.getByType(603);
//        List<String> color = new ArrayList<String>();
//        List<Float> colorcritical = new ArrayList<Float>();
//        for(HashMap<String, Object> map: colorLists){
//            String colorValue = (String)map.get("colorvalue");
//            Float criticalvalue = (Float)map.get("criticalvalue");
//            color.add(colorValue);
//            colorcritical.add(criticalvalue);
//        }
//        /*
//        Iterator<HashMap<String, Object>> listIterator = lists2.iterator();
//        while(listIterator.hasNext()){
//            HashMap<String, Object> nextMap = listIterator.next();
//            Set<String> keySet = nextMap.keySet();
//            Iterator<String> iterator = keySet.iterator();
//            while (iterator.hasNext()){
//                String nextKey = iterator.next();
//                if("tem".equals(nextKey)){
//                    Float temValue = (Float)nextMap.get(nextKey);
//                    if(temValue > 100){
//                        listIterator.remove();
//                    }
//                }else {
//                    continue;
//                }
//            }
//        }
//         */
//        System.out.println(lists1);
//        System.out.println(lists2);
//        System.out.println("list2:" + lists2.size());
//        System.out.println(colorLists);
//        Map<String,Object> result = new HashMap<String, Object>();
//        result.put("lon_max",lists1.get(0).get("max(lon)"));
//        result.put("lon_min",lists1.get(0).get("min(lon)"));
//        result.put("lat_max",lists1.get(0).get("max(lat)"));
//        result.put("lat_min",lists1.get(0).get("min(lat)"));
//        //Float lon_min = (Float) lists1.get(1);
//        //Float lat_max = (Float) lists1.get(2);
//        //Float lon_min = (Float) lists1.get(3);
//        Integer n_lon = (int)(((Float)lists1.get(0).get("max(lon)") - (Float) lists1.get(0).get("min(lon)")) / 0.01);
//        Integer n_lat = (int)(((Float)lists1.get(0).get("max(lat)") - (Float) lists1.get(0).get("min(lat)")) / 0.01);
//        result.put("n_lon",n_lon);
//        result.put("n_lat",n_lat);
//        result.put("color",color);
//        result.put("color_levels",colorcritical);
//        result.put("dataMap",lists2);
//        String filePath = "D:/Data/tem_603/20200701/20200701210000_603";
//        String imageFormat = ".svg";
//        result.put("filePath",filePath);
//        result.put("imageFormat",imageFormat);
//        System.out.println(result);
//        JSON parse = JSONUtil.parse(result);
//        String parseStr = parse.toString();
//        System.out.println(parseStr);
//        try {
//            writeFile(parseStr,"20200701210000_603.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void queryprs(){
//        String tableName = "data_atstation_202007";
//        String datetime = "2020-07-06 09:00:00";
//        String fieldStr1 = "max(lon),min(lon),max(lat),min(lat)";
//        String fieldStr2 = "lon,lat,tem";
//        Float filterStr = 10000.0f;
//        String typeStr = "prs";
//        List<HashMap<String, Object>> lists1 = atstationMapper.getByDatetime(tableName, datetime, fieldStr1,typeStr,filterStr);
//        List<HashMap<String, Object>> lists2 = atstationMapper.getByDatetime(tableName, datetime, fieldStr2,typeStr,filterStr);
//        System.out.println("list2:" + lists2.size());
//        List<HashMap<String, Object>> colorLists = colorValueMapper.getByType(602);
//        List<String> color = new ArrayList<String>();
//        List<Float> colorcritical = new ArrayList<Float>();
//        for(HashMap<String, Object> map: colorLists){
//            String colorValue = (String)map.get("colorvalue");
//            Float criticalvalue = (Float)map.get("criticalvalue");
//            color.add(colorValue);
//            colorcritical.add(criticalvalue);
//        }
//        /*
//        Iterator<HashMap<String, Object>> listIterator = lists2.iterator();
//        while(listIterator.hasNext()){
//            HashMap<String, Object> nextMap = listIterator.next();
//            Set<String> keySet = nextMap.keySet();
//            Iterator<String> iterator = keySet.iterator();
//            while (iterator.hasNext()){
//                String nextKey = iterator.next();
//                if("tem".equals(nextKey)){
//                    Float temValue = (Float)nextMap.get(nextKey);
//                    if(temValue > 100){
//                        listIterator.remove();
//                    }
//                }else {
//                    continue;
//                }
//            }
//        }
//         */
//        System.out.println(lists1);
//        System.out.println(lists2);
//        System.out.println("list2:" + lists2.size());
//        System.out.println(colorLists);
//        Map<String,Object> result = new HashMap<String, Object>();
//        result.put("lon_max",lists1.get(0).get("max(lon)"));
//        result.put("lon_min",lists1.get(0).get("min(lon)"));
//        result.put("lat_max",lists1.get(0).get("max(lat)"));
//        result.put("lat_min",lists1.get(0).get("min(lat)"));
//        //Float lon_min = (Float) lists1.get(1);
//        //Float lat_max = (Float) lists1.get(2);
//        //Float lon_min = (Float) lists1.get(3);
//        Integer n_lon = (int)(((Float)lists1.get(0).get("max(lon)") - (Float) lists1.get(0).get("min(lon)")) / 0.01);
//        Integer n_lat = (int)(((Float)lists1.get(0).get("max(lat)") - (Float) lists1.get(0).get("min(lat)")) / 0.01);
//        result.put("n_lon",n_lon);
//        result.put("n_lat",n_lat);
//        result.put("color",color);
//        result.put("color_levels",colorcritical);
//        result.put("dataMap",lists2);
//        System.out.println(result);
//        JSON parse = JSONUtil.parse(result);
//        String parseStr = parse.toString();
//        System.out.println(parseStr);
//        try {
//            writeFile(parseStr,"20200706090000_602.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    @Test
//    public void atstion(){
//        String tableName = "data_atstation_202007";
//        String datetime = "2020-07-06 09:00:00";
//        String fieldStr = "station_id_d,lon,lat,tem,alti";
//        Float filterStr = 100.0f;
//        String typeStr = "tem";
//        List<HashMap<String, Object>> lists = atstationMapper.getByDatetime(tableName, datetime, fieldStr,typeStr,filterStr);
//        System.out.println(lists);
//        try {
//            writeFile2(lists,"atstation_20200706090000_603.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//
//    public void writeFile(String content,String filePath) throws IOException {
//        File file = new File(filePath);
//        if(!file.exists()){
//            file.createNewFile();
//        }
//        System.out.println(content);
//        BufferedWriter bw = null;
//        try {
//            bw = new BufferedWriter(new FileWriter(file));
//            bw.write(content);
//        }catch (Exception e){
//            e.getStackTrace();
//        }finally {
//            if(bw != null){
//                bw.close();
//            }
//        }
//    }
//
//
//    public void writeFile2(List<HashMap<String, Object>> lists,String filePath) throws IOException {
//        File file = new File(filePath);
//        if(!file.exists()){
//            file.createNewFile();
//        }
//        BufferedWriter bw = null;
//        try {
//            bw = new BufferedWriter(new FileWriter(file));
//            for (HashMap<String, Object> map:lists){
//                Float lon = (Float)map.get("lon");
//                Float lat = (Float)map.get("lat");
//                Float tem = (Float)map.get("tem");
//                Integer station_id_d = (Integer)map.get("station_id_d");
//                Float alti = (Float)map.get("alti");
//                String content = station_id_d + "\t" + lon + "\t" + lat + "\t" + alti + "\t" + tem;
//                bw.write(content);
//                bw.write("\n");
//            }
//        }catch (Exception e){
//            e.getStackTrace();
//        }finally {
//            if(bw != null){
//                bw.close();
//            }
//        }
//    }
//
//    @Test
//    public void testDate(){
//        String datetime = "2020-07-01 00:00:00";
//        DateTime time = DateUtil.parse(datetime, "yyyyMMddHHmmss");
//        System.out.println(time.getTime());
//
//    }
//
//
//
//
//}
