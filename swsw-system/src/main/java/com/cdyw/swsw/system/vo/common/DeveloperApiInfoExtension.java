package com.cdyw.swsw.system.vo.common;

import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jovi
 */
public class DeveloperApiInfoExtension implements VendorExtension<List<DeveloperApiInfo>> {

    private final String EXTEND_API_INFO = "x-developers";

    private final List<DeveloperApiInfo> developerApiInfoExtensions = new ArrayList<>();

    public DeveloperApiInfoExtension addDeveloper(DeveloperApiInfo developerApiInfo) {
        developerApiInfoExtensions.add(developerApiInfo);
        return this;
    }

    @Override
    public String getName() {
        return EXTEND_API_INFO;
    }

    @Override
    public List<DeveloperApiInfo> getValue() {
        return developerApiInfoExtensions;
    }
}
