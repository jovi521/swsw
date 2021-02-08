package com.cdyw.swsw.common.domain.entity.commondata;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ThresholdVo {

    private Integer userId;
    private List<Map<String, Object>> params;
}
