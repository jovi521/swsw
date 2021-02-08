package com.cdyw.swsw.common.domain.entity.menu;

import lombok.Data;

import java.util.List;

@Data
public class Diymode {
    private Integer modeId;
    private String name;
    private List<String> idList;
    private Boolean idOpen = false;
    private Boolean isShortcut;
}
