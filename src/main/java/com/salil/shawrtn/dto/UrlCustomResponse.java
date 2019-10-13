package com.salil.shawrtn.dto;

public class UrlCustomResponse {

    public static final int SUCCESSFUL = 0;

    protected boolean success;
    protected String message;
    protected Integer code;

    public UrlCustomResponse() {
    }

    public UrlCustomResponse(boolean success, String message, Integer code) {
        this.success = success;
        this.message = message;
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }


    @Override
    public String toString() {
        return String.format("UrlCustomResponse[success:%s, message: %s, code: %s]", String.valueOf(success), message, String.valueOf(code));
    }
}
