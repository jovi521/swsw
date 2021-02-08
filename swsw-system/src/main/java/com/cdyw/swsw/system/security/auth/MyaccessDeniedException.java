package com.cdyw.swsw.system.security.auth;

import org.springframework.security.access.AccessDeniedException;

public class MyaccessDeniedException extends AccessDeniedException {


    public MyaccessDeniedException(String msg) {
        super(msg);
    }
}
