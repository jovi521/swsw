package com.cdyw.swsw.common.domain.ao.enums;

import lombok.Getter;

/**
 * 文件类型（后缀区分）枚举类
 *
 * @author jovi
 */
@Getter
public enum FileTypeEnum {
    /**
     * word-doc
     */
    FILE_TYPE_DOC("doc"),
    /**
     * word-docx
     */
    FILE_TYPE_DOCX("docx"),
    /**
     * excel-xls
     */
    FILE_TYPE_XLS("xls"),
    /**
     * excel-xlsx
     */
    FILE_TYPE_XLSX("xlsx"),
    /**
     * ppt
     */
    FILE_TYPE_PPT("ppt"),
    /**
     * pdf
     */
    FILE_TYPE_PDF("pdf"),
    /**
     * html
     */
    FILE_TYPE_HTML("html"),
    /**
     * txt
     */
    FILE_TYPE_TXT("txt"),
    /**
     * zip
     */
    FILE_TYPE_ZIP("zip"),
    /**
     * png
     */
    FILE_TYPE_PNG("png");

    private final String suffix;

    FileTypeEnum(String suffix) {
        this.suffix = suffix;
    }
}
