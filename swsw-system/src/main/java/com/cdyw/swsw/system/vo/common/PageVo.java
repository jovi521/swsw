package com.cdyw.swsw.system.vo.common;

import lombok.Data;

@Data
public class PageVo {

    /**
     * 当前页号
     */
    private final int currentPage = 1;

    /**
     * 当前页号
     */
    private int startIndex = 0;

    /**
     * 页面大小(默认查询最大值)
     */
    private int pageSize = 999999;

    /**
     * 总记录数
     */
    private int totalNumber;
}