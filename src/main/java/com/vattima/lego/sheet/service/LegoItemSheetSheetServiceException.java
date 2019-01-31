package com.vattima.lego.sheet.service;

public class LegoItemSheetSheetServiceException extends RuntimeException {
    public LegoItemSheetSheetServiceException() {
        super();
    }

    public LegoItemSheetSheetServiceException(String message) {
        super(message);
    }

    public LegoItemSheetSheetServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public LegoItemSheetSheetServiceException(Throwable cause) {
        super(cause);
    }

    protected LegoItemSheetSheetServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
