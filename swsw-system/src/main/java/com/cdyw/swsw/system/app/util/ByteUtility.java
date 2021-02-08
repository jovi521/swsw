package com.cdyw.swsw.system.app.util;

import java.nio.charset.Charset;

/**
 * 小端字节序
 *
 * @author cdyw
 */
public class ByteUtility {

    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    public static byte[] getBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data >> 8);
        return bytes;
    }

    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    public static byte[] getBytes(String data) {
        return getBytes(data, "UTF-8");
    }

    public static short getShort(byte[] bytes, int index) {
        return (short) ((0xff & bytes[index + 0]) | (0xff00 & (bytes[index + 1] << 8)));
    }

    public static char getChar(byte[] bytes, int index) {
        return (char) ((0xff & bytes[index + 0]) | (0xff00 & (bytes[index + 1] << 8)));
    }

    public static int getInt(byte[] bytes, int index) {
        return (0xff & bytes[index + 0]) | (0xff00 & (bytes[index + 1] << 8))
                | (0xff0000 & (bytes[index + 2] << 16))
                | (0xff000000 & (bytes[index + 3] << 24));
    }

    public static long getLong(byte[] bytes, int index) {
        return (0xffL & (long) bytes[index + 0])
                | (0xff00L & ((long) bytes[index + 1] << 8))
                | (0xff0000L & ((long) bytes[index + 2] << 16))
                | (0xff000000L & ((long) bytes[index + 3] << 24));
//				| (0xff00000000L & ((long) bytes[index + 4] << 32))
//				| (0xff0000000000L & ((long) bytes[index + 5] << 40))
//				| (0xff000000000000L & ((long) bytes[index + 6] << 48))
//				| (0xff00000000000000L & ((long) bytes[index + 7] << 56));
    }

    public static float getFloat(byte[] bytes, int index) {
        return Float.intBitsToFloat(getInt(bytes, index));
    }

    public static double getDouble(byte[] bytes, int index) {
        long l = getLong(bytes, index);
        return Double.longBitsToDouble(l);
    }

    public static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }

    public static String getString(byte[] bytes) {
        return getString(bytes, "UTF-8");
    }
}
