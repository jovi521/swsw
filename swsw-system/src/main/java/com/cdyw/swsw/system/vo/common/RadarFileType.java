package com.cdyw.swsw.system.vo.common;


//雷达推送文件指定存放类型
public enum RadarFileType {
    Param1(10, "/MONITOR/"),
    Param2(20, "/WNDFFT/"),
    Param3(31, "/WNDOBS/ROBS/"),
    Param4(32, "/WNDOBS/HOBS/"),
    Param5(33, "/WNDOBS/OOBS/"),
    Param6(40, "/WNDRAD/"),
    param7(50, "/TEPFFT/"),
    param8(60, "/TEPOBS/"),
    param9(70, "/TEPRAD/");
    public Integer code;
    public String name;

    RadarFileType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    //普通方法 
    public static String getName(int code) {
        for (RadarFileType c : RadarFileType.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}