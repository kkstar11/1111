package com.xianyu.util;

public class Result<T> {

    private int code;
    private String message;
    private T data;
    private boolean success;  // 新增
    private Result(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data, true);
    }
    public static <T> Result<T> failure(String message) {
        return new Result<>(ResponseCode.FAILURE.getCode(), message, null, false);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}