package com.cdyw.swsw.common.domain.ao.enums;

import lombok.Getter;

/**
 * 文件状态枚举类：default 新增的时候，默认的原始文件； temp 预览的时候，临时的修改文件； final 发布的时候，最终的修改文件
 *
 * @author jovi
 */
@Getter
public enum FileStatusEnum {
    /**
     * insert-default
     */
    FILE_STATUS_DEFAULT("default"),
    /**
     * preview-temp
     */
    FILE_STATUS_TEMP("temp"),
    /**
     * publish-final
     */
    FILE_STATUS_FINAL("final");

    private final String status;

    FileStatusEnum(String status) {
        this.status = status;
    }
}
