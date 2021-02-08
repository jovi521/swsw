//package com.cdyw.swsw.system;
//
//import cn.hutool.core.io.IoUtil;
//import cn.hutool.core.util.StrUtil;
//import com.cdyw.swsw.common.domain.ao.CommonRadarParseTxt;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//
//import java.io.*;
//import java.nio.ByteBuffer;
//import java.nio.channels.FileChannel;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author jovi
// */
//public class IoTest {
//
//    @Test
//    public void testNio() throws IOException {
//        //1.创建一个RandomAccessFile（随机访问文件）对象，
//        RandomAccessFile raf = new RandomAccessFile("D:\\niodata.txt", "rw");
////通过RandomAccessFile对象的getChannel()方法。FileChannel是抽象类。
//        FileChannel inChannel = raf.getChannel();
////2.创建一个读数据缓冲区对象
//        ByteBuffer buf = ByteBuffer.allocate(48);
//
////3.从通道中读取数据
//        int bytesRead = inChannel.read(buf);
//
////创建一个写数据缓冲区对象
//        ByteBuffer buf2 = ByteBuffer.allocate(48);
//
////写入数据
//        buf2.put("000".getBytes());
//        buf2.flip();
//        inChannel.write(buf);
//        while (bytesRead != -1) {
//            System.out.println("Read " + bytesRead);
//
////Buffer有两种模式，写模式和读模式。在写模式下调用flip()之后，Buffer从写模式变成读模式。
//            buf.flip();
//
////如果还有未读内容
//            while (buf.hasRemaining()) {
//                System.out.print((char) buf.get());
//            }
//
////清空缓存区
//            buf.clear();
//            bytesRead = inChannel.read(buf);
//        }
//
//
////关闭RandomAccessFile（随机访问文件）对象
//        raf.close();
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testTxtToJson() throws IOException {
//        CommonRadarParseTxt txt = new CommonRadarParseTxt();
//        File file = new File("D:/Data/satellite/Z9280/20200716/20180710_182600.00.007.000_18.5_R2.txt");
//        InputStream inputStream = new FileInputStream(file);
//        List<String> list = new ArrayList<>();
//        list = IoUtil.readLines(inputStream, StandardCharsets.UTF_8, list);
//        list.removeAll(Collections.singleton("")); // 去除空值
//        list.removeIf(s -> s.contains("<"));// 去除<不必要的元素
//
//        System.out.println(list.toString());
//        // 遍历集合
//        for (String s : list) {
//            String sBefore = StrUtil.subBefore(s, ":", false);
//            String sAfter = StrUtil.subAfter(s, ":", false);
//            if ("ProductID".equals(sBefore)) {
//                txt.setProductID(sAfter);
//            } else if ("ProductName".equals(sBefore)) {
//                txt.setProductName(sAfter);
//            } else if ("StartTime".equals(sBefore)) {
//                txt.setStartTime(sAfter);
//            } else if ("EndTime".equals(sBefore)) {
//                txt.setEndTime(sAfter);
//            } else if ("StationNo".equals(sBefore)) {
//                txt.setStationNo(sAfter);
//            } else if ("StationName".equals(sBefore)) {
//                txt.setStationName(sAfter);
//            } else if ("RadarCoor".equals(sBefore)) {
//                txt.setRadarCoor(sAfter);
//            } else if ("MaxDis".equals(sBefore)) {
//                txt.setMaxDis(sAfter);
//            } else if ("Height".equals(sBefore)) {
//                txt.setHeight(sAfter);
//            } else if ("Projection".equals(sBefore)) {
//                txt.setProjection(sAfter);
//            } else if ("LeftBottomCoor".equals(sBefore)) {
//                txt.setLeftBottomCoor(sAfter);
//            } else if ("RightTopCoor".equals(sBefore)) {
//                txt.setRightTopCoor(sAfter);
//            } else if ("WidthHeight".equals(sBefore)) {
//                txt.setWidthHeight(sAfter);
//            } else if ("RadarPos".equals(sBefore)) {
//                txt.setRadarPos(sAfter);
//            }
//        }
//        //  TODO 不行就将list拆解成对象，对象更直观更好操作
//        ObjectMapper mapper = new ObjectMapper();
//// 将文件名也传参数过去
//        txt.setFileName(StrUtil.subBefore(file.getName(), ".", true));
//        // 转化为json
//        String txtJson = mapper.writeValueAsString(txt);
//        JsonNode jsonNode = mapper.readTree(txt.toString());
//        Object[] objects = list.toArray();
//        StringBuilder content = new StringBuilder();
//        for (String l : list) {
//            content.append(l);
//        }
//        byte[] bytes = new byte[100];
////        Map<String,Object> map = mapper.readValue(content.toString(), Map.class);
//        Map<String, Object> map = mapper.readValue("aaa:111,bbb:222,ccc:333", Map.class);
//        System.out.println("sss");
//    }
//
//    @Test
//    public void testJson() throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        String str = "{\"data\":{\"birth_day\":7,\"birth_month\":6},\"errcode\":0,\"msg\":\"ok\",\"ret\":0}";
//        String str1 = "'data':111";
////        String strJson = mapper.writeValueAsString(str);
////        System.out.println(strJson);
//        JsonNode jsonNode = mapper.readTree(str1);
//        jsonNode.path("");
//        Map<String, Object> map = mapper.readValue(str, Map.class);
//        System.out.println(map);
//    }
//}
