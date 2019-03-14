package com.anbang.qipai.robot.exceptions;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:20 PM
 * @Version 1.0
 */
public class AnBangException extends RuntimeException {
    private int errorCode;
    private String message;
    /**
     * 异常栈信息 非必需
     */
    private Exception exception;


    public AnBangException(Throwable cause) {
        super(cause);
    }

    public AnBangException(Throwable cause, String message) {
        super(cause);
        this.errorCode = 1;
        this.message = message;
    }

    public AnBangException(String message) {
        super(message);
        this.errorCode = 1;
        this.message = message;
    }

    public AnBangException(String message,
                           Exception exception) {
        super();
        this.exception = exception;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
