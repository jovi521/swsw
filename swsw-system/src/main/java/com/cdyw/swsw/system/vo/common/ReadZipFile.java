package com.cdyw.swsw.system.vo.common;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class ReadZipFile {


    //读取txt文件
    public static void readTxtFile(String filePath, HttpServletResponse response) {


        try {

            //response.setContentType("text/plain;charset=utf-8");
            //response.setHeader("Content-Disposition", "attachment;filename=" + filePath);
            ServletOutputStream out = response.getOutputStream();
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(filePath));
            //定义个数组，由于读取缓冲流中的内容
            byte[] buffer = new byte[1024];
            //while循环一直读取缓冲流中的内容到输出的对象中
            int i = 0;
            while ((i = buf.read(buffer)) > 0) {
                out.write(buffer, 0, i);
            }
            //写出到请求的地方
            out.flush();

            if (buf != null) {
                buf.close();
            }
            if (out != null) {
                out.close();
            }


        } catch (Exception e) {
            //putDataToWeb(response);
            //TODO 读取文件错误
            System.out.println("文本读取错误");
            e.printStackTrace();
        }


    }
}
