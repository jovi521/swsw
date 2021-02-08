package com.cdyw.swsw.system.app.config;

public class UsernameOrPasswordError extends RuntimeException {

    public UsernameOrPasswordError() {
        super();
    }

    public UsernameOrPasswordError(String message) {
        super(message);
    }

    public UsernameOrPasswordError(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameOrPasswordError(Throwable cause) {
        super(cause);
    }

    protected UsernameOrPasswordError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
