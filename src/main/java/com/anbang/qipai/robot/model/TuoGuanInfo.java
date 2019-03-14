package com.anbang.qipai.robot.model;

/**
 * @Author: 吴硕涵
 * @Date: 2019/3/4 3:52 PM
 * @Version 1.0
 */

//作为返回结果使用
public class TuoGuanInfo {
    private String playerId;
    private String token;

    public TuoGuanInfo(){}


    public TuoGuanInfo(String playerId, String token) {
        this.playerId = playerId;
        this.token = token;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
