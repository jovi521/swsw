package com.cdyw.swsw.common.common.util;

import java.io.*;

/**
 * 将文件写入到硬盘
 *
 * @author cdyw
 */
public class WriteFileUtil {


    public static void writeFile(String content, String path) throws IOException {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(path));
            bw.write(content);
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    /**
     * 将源文件复制到目标路径
     *
     * @param sourceFile
     * @param targetFile
     */
    public static void copyFile(String sourceFile, String targetFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(targetFile);
            byte[] buff = new byte[1024];
            int len;
            while ((len = fis.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public static void copyFileUsingFileStreams(File source, File dest)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

}
