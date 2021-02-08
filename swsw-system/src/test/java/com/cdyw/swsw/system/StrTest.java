package com.cdyw.swsw.system;

import cn.hutool.core.util.StrUtil;
import org.junit.jupiter.api.Test;

/**
 * @author jovi
 */
public class StrTest {

    @Test
    public void testStrSub() {
        String s = "StartTime:2018/07/10 18:03:00";
        String sBefore = StrUtil.subBefore(s, ":", false);
        System.out.println(sBefore);
    }
}
