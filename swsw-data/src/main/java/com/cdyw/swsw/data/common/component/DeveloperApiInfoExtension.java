package com.cdyw.swsw.data.common.component;

import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jovi
 */
public class DeveloperApiInfoExtension implements VendorExtension<List<DeveloperApiInfo>> {

    private final List<DeveloperApiInfo> developerApiInfoExtensions = new ArrayList<>();

    public DeveloperApiInfoExtension addDeveloper(DeveloperApiInfo developerApiInfo) {
        developerApiInfoExtensions.add(developerApiInfo);
        return this;
    }

    @Override
    public String getName() {
        return "x-developers";
    }

    @Override
    public List<DeveloperApiInfo> getValue() {
        return developerApiInfoExtensions;
    }
}
