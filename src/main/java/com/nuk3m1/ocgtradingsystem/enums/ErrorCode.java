package com.nuk3m1.ocgtradingsystem.enums;
public enum ErrorCode {
    NOT_FOUND_ERROR(404, "请求数据不存在"),
    PARAM_IS_WRONG(400,"输入参数错误"),
    PERMISSION_ERROR(401,"权限错误"),
    SYSTEM_ERROR(500,"系统内部错误"),
    OPERATION_ERROR(501, "操作失败"),
    NOT_LOGIN_ERROR(402,"未登录");


    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code , String message) {
        this.code = code ;
        this.message = message;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}