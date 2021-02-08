package com.cdyw.swsw.common.common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

public class Base64Convert {

    /**
     * @Description： 图片转化成base64字符串
     * @param: path
     * @Return:
     */
    public static String GetImageStr(String path) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        //待处理的图片
        String imgFile = path;
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        //返回Base64编码过的字节数组字符串
        return encoder.encode(data);
    }

    /**
     * @Description： 图片转化成base64字符串
     * @param: path
     * @Return:
     */
    public static String GetImageStr(InputStream is) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        //待处理的图片
        byte[] data = null;
        BufferedInputStream in = null;
        //读取图片字节数组
        try {
            in = new BufferedInputStream(is);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        //返回Base64编码过的字节数组字符串
        return encoder.encode(data);
    }


    /**
     * @Description： base64字符串转化成图片
     * @param: imgStr
     * @Return:
     */
    public static boolean GenerateImage(String imgStr, String photoname) {
        //对字节数组字符串进行Base64解码并生成图片
        //图像数据为空
        if (imgStr == null)
            return false;


        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    //调整异常数据
                    b[i] += 256;
                }
            }
            //生成jpeg图片
            //System.currentTimeMillis()
            //新生成的图片
            OutputStream out = new FileOutputStream(photoname);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
