package com.cdyw.swsw.common.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cdyw.swsw.common.domain.ao.txt.CommonRadarParseTxt;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 天气雷达、相控阵雷达解析后的txt文件内容封装成对象
 *
 * @author jovi
 */
public class CommonRadarParseTxtUtil {

    /**
     * 解析后的txt文件封装成对象
     *
     * @param txtFile 解析后的 txt 文件
     * @return CommonRadarParseTxt
     */
    public static CommonRadarParseTxt readParseTxt(File txtFile) {
        List<String> strings = FileUtil.readLines(txtFile, StandardCharsets.UTF_8);
        strings.removeIf(String::isEmpty);
        strings.removeIf(s -> s.contains("<"));
        Map<String, Object> values = new HashMap<>(14);
        for (String s : strings) {
            values.put(StrUtil.lowerFirst(s.substring(0, s.indexOf(":"))), s.substring(s.indexOf(":") + 1));
        }
        return BeanUtil.mapToBean(values, CommonRadarParseTxt.class, true, CopyOptions.create());
    }
}
