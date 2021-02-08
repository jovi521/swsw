package com.cdyw.swsw.system.app.config;

public class VerifyCodeError extends RuntimeException {

    public VerifyCodeError() {
        super();
    }

    public VerifyCodeError(String message) {
        super(message);
    }

    public VerifyCodeError(String message, Throwable cause) {
        super(message, cause);
    }

    public VerifyCodeError(Throwable cause) {
        super(cause);
    }

    protected VerifyCodeError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
