package com.cdyw.swsw.system.app.util;

import java.io.*;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ReadZip {

    private static final ByteBuffer buffer = ByteBuffer.allocate(8);

    /**
     * 最新读取二进制文件，只读取一次
     *
     * @param bt     文件字节数据
     * @param type   1short 2int 3long 4string 5float 6long int 7char占1个字节的字符串 8 short int 1个字节的int
     * @param offset 开始位置
     * @param size   字节大小
     * @return
     */
    public static Object ReadZipFileObject(byte[] bt, int type, int offset, int size, String name) {
        Object object = null;
        try {
            byte[] b;
            if (type == 1) {//short类型
                b = copyBytes(bt, offset, size);
                int key = byte2Short(b);
                object = key;
            } else if (type == 2) {//int
                b = copyBytes(bt, offset, size);
                int key = byteToInt(b);
                object = key;
            } else if (type == 3) {//long
                b = copyBytes(bt, offset, size);
                long key = byteToLong(b);
                object = key;
            } else if (type == 4) {//string
                b = copyBytes(bt, offset, size);
                String key = byte2String(b);
                object = key;
            } else if (type == 5) {//float
                b = copyBytes(bt, offset, size);
                float key = byte2Float(b);
                object = key;
            } else if (type == 6) {//long int
                b = copyBytes(bt, offset, size);
                long key = bytesToLongInt(b);
                object = key;
            } else if (type == 7) {
                b = copyBytes(bt, offset, size);
                String key = byteToChar(b);
                object = key;
            } else if (type == 8) {//byte 转char 占一个字节
                b = copyBytes(bt, offset, size);
                String key = byteToOneChar(b);
                object = key;
            } else {
                b = copyBytes(bt, offset, size);
                int key = byte2ShortChar(b);
                object = key;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 读取压缩中的二进制文件，并将二进制文件转为byte[]，方便后续的二进制数组截取，详细内容如下
     *
     * @param file
     * @throws Exception
     */
    @SuppressWarnings("resource")
    public static byte[] readZipFile(String file) throws Exception {

        ZipFile zf = new ZipFile(file);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
//		Charset gbk = Charset.forName("gbk");
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;// 对应的就是压缩中的具体文件
        while ((ze = zin.getNextEntry()) != null) {
            if (ze.isDirectory()) {
            } else {
                System.err.println("file - " + ze.getName() + " : " + ze.getSize() + " bytes");
                if (ze.getSize() > Integer.MAX_VALUE) {
                    System.out.println("file too big...");
                    return null;
                }
                DataInputStream read = new DataInputStream(zf.getInputStream(ze));

                byte[] buffer = new byte[(int) ze.getSize()];
                int offset = 0;
                int numRead = 0;
                while (offset < buffer.length && (numRead = read.read(buffer, offset, buffer.length - offset)) >= 0) {
                    offset += numRead;
                }
                // 确保所有数据均被读取
                if (offset != buffer.length) {
                    throw new IOException("Could not completely read file " + ze.getName());
                }

                read.close();
                zin.closeEntry();
                ze.clone();
                in.close();
                zf.close();

                return buffer;
            }
        }

        return null;
    }

    /**
     * 截取字节数组
     *
     * @param bytes  原始数组
     * @param offset 偏移量
     * @param size   长度
     * @return 新的字节数组
     */
    public static byte[] copyBytes(byte[] bytes, int offset, int size) {
        byte[] newBytes = new byte[size];
        System.arraycopy(bytes, offset, newBytes, 0, size);
        return newBytes;
    }

    /**
     * 1将字节数组转为short类型（注：由于转换后为有符号short，但我们需要无符号short，所以与上0x0FFFF，返回int类型）
     *
     * @param b
     * @return
     */
    public static int byte2Short(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * 2注释：字节数组到int的转换！
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte[] b) {
        int s = 0;
        int s0 = b[0] & 0xff;// 最低位
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    // 3byte数组转成long
    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    // 4byte[] 转string
    public static String byte2String(byte[] b) throws UnsupportedEncodingException {
        if (b == null || b.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");
        String str = new String(b, "gbk");
//		String str = new String(b,"ISO-8859-1");
        return str;
    }

    //5byte[] 转float
    public static float byte2Float(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    //6byte[] 转long int
    public static long bytesToLongInt(byte[] b) {
        buffer.put(b, 0, b.length);
        buffer.flip();
        return buffer.getLong();
    }

    //7byte[] 转char
    public static String byteToChar(byte[] b) {

        if (b == null || b.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");
        String str = "";
        try {
//			str = new String(b,"ISO-8859-1");
//			str = new String(b,"utf-8");
            str = new String(b, "gbk");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;

    }

    //7 byte[] 数组转 char-->string
    public static String byteToOneChar(byte[] bytes) throws UnsupportedEncodingException {
        char c = (char) bytes[0];
        int b = c;
        return String.valueOf(b);
    }

    //8byte[] 转占1位的int
    public static int byte2ShortChar(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        s = s0;
        return s;
    }

    //整数相除保留n位小数
    public static double divToDouble(int target, int param, int keepnum) {
        double data = 0;
        try {
            data = new BigDecimal((float) target / param).setScale(keepnum, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //相加
    public static double addToDouble(double param1, double param2) {
        double data = 0;
        try {
            data = param1 + param2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * //读取雷达参数  现未使用
     *
     * @param filename 文件名称
     * @param type     1short 2int 3long 4string 5float 6long int
     * @param offset   开始位置
     * @param size     字节大小
     * @return
     */
    public static Object ReadZipFileRadarParams(String filename, int type, int offset, int size, String name) {
        Object object = null;
        try {
            byte[] bt = readZipFile(filename);
            byte[] b;
            if (type == 1) {//short类型
                b = copyBytes(bt, offset, size);
                int key = byte2Short(b);
                object = key;
            } else if (type == 2) {//int
                b = copyBytes(bt, offset, size);
                int key = byteToInt(b);
                object = key;
            } else if (type == 3) {//long
                b = copyBytes(bt, offset, size);
                long key = byteToLong(b);
                object = key;
            } else if (type == 4) {//string
                b = copyBytes(bt, offset, size);
                String key = byte2String(b);
                object = key;
            } else if (type == 5) {//float
                b = copyBytes(bt, offset, size);
                float key = byte2Float(b);
                object = key;
            } else {//long int
                b = copyBytes(bt, offset, size);
                long key = bytesToLongInt(b);
                object = key;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 读取二进制文件，并将二进制文件转为byte[]，方便后续的二进制数组截取，详细内容如下 (未用此方法)
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        fi.close();
        return buffer;
    }
}
