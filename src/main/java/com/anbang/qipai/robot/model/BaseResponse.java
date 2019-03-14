package com.anbang.qipai.robot.model;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:22 PM
 * @Version 1.0
 */
public class BaseResponse<T> {
    private int errCode = 0;
    private String msg = "执行成功";
    private T data;

    public BaseResponse() {
    }

    public int getErrCode() {
        return this.errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
