package com.cdyw.swsw.common.domain.entity.fusion12h;

import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Fusion12h 解析实体类
 *
 * @author liudong
 * @modified jovi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Fusion12h extends FileEntity {

    private Long forecastTime;
}
