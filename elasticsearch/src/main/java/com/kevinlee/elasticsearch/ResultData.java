package com.kevinlee.elasticsearch;

public class ResultData<T> {
    private int status;
    private String message;
    private T data;

    // 构造函数
    public ResultData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // 静态工厂方法
    public static <T> ResultData of(int status, String message) {
        return new ResultData<>(status, message, null);
    }

    // Getter和Setter方法
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // toString方法便于打印
    @Override
    public String toString() {
        return "ResultData{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
