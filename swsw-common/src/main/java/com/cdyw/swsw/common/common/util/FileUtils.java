package com.cdyw.swsw.common.common.util;

import java.io.*;
import java.util.List;

/**
 * 操作文件的工具类
 *
 * @author cdyw
 */
public class FileUtils {

    /**
     * 通过字符流的方式读取指定路径的内容
     *
     * @param filepath
     * @return
     */
    public static String readCharByPath(String filepath) throws IOException {
        FileReader fr = null;
        BufferedReader br = null;
        String content = "";
        try {
            fr = new FileReader(filepath);
            br = new BufferedReader(fr);
            char[] buff = new char[1024];
            int len = 0;
            while ((len = br.read(buff)) != -1) {
                String line = new String(buff, 0, len);
                content += line;
            }
        } finally {
            fr.close();
        }
        return content;
    }

    /**
     * 通过字符流的方式读取指定路径的内容
     *
     * @param is
     * @return
     */
    public static String readCharByPath(InputStream is) throws IOException {
        BufferedReader br = null;
        String content = "";
        try {
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);
            char[] buff = new char[1024];
            int len = 0;
            while ((len = br.read(buff)) != -1) {
                String line = new String(buff, 0, len);
                content += line;
            }
        } finally {
            br.close();
            is.close();
        }
        return content;
    }

    /**
     * 读取当前项目下的文件内容
     *
     * @param filepath
     * @return
     */
    public static String readCharInCurrentProject(String filepath) {
        InputStream is = FileUtils.class.getClassLoader().getResourceAsStream(filepath);
        InputStreamReader isr = null;
        String content = "";
        try {
            isr = new InputStreamReader(is, "UTF-8");
            char[] buff = new char[1024];
            int len = 0;
            while ((len = isr.read(buff)) != -1) {
                content += new String(buff, 0, len);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                isr.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }


    /*
        读取某个路径下的文件名
     */
    public static void getAllFileName(String filePath, List<String> listFileName) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isDirectory()) {
                getAllFileName(file1.getAbsolutePath(), listFileName);
            } else {
                String name1 = file1.getName();
                listFileName.add(name1);
            }
        }
    }

    /*
    检查文件是否正在写入，ture代表正在写入，false代表写入完成
    */
    public static boolean isFileWriting(String fileName) throws Exception {
        File file = new File(fileName);
        if (file.exists()) {
            if (file.renameTo(file)) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }

    }


}
