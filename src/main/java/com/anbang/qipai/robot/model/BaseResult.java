package com.anbang.qipai.robot.model;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:22 PM
 * @Version 1.0
 */
public class BaseResult {
    /** 成功失败标识 (悲观模式默认失败)*/
    /** 环境变了   现在默认成功*/
    protected boolean         success          = true;

    /** 返回信息 */
    protected String          message          = BaseResultCodeEnum.SUCCESS.getMessage();

    public BaseResult() {
    };

    /**
     * 构造方法
     * @param success       成功标识
     */
    public BaseResult(boolean success) {
        if (!success) {
            this.success = false;
            this.message = BaseResultCodeEnum.SYSTEM_ERROR.getMessage();
        }
    }

    /**
     * @param success
     * @param message
     */
    public BaseResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BaseResult markResult(boolean success, String message){
        this.success = success;
        this.message = message;

        return this;
    }

    @Override
    public String toString() {

        return getClass().getSimpleName() + "[success:" + success + ",message:" + message + "]";

    }

    /**
     * Getter method for property <tt>success</tt>.
     *
     * @return property value of success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Getter method for property <tt>message</tt>.
     *
     * @return property value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter method for property <tt>message</tt>.
     *
     * @param message value to be assigned to property message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Setter method for property <tt>success</tt>.
     *
     * @param success value to be assigned to property success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
