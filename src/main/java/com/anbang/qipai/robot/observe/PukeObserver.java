package com.anbang.qipai.robot.observe;

/**
 * @Author: 吴硕涵
 * @Date: 2019/3/4 1:18 PM
 * @Version 1.0
 */
public class PukeObserver implements Observer {

    private String playerId;
    private String gameToken;
    private String gameId;
    private String wsUrl;

    public void setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setGameToken(String gameToken) {
        this.gameToken = gameToken;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public void tuoguan() {


    }
}
