package com.xianyu.util;

public enum ResponseCode {
    SUCCESS(200, "登录成功！"),
    FAILURE(500, "登陆失败！");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

