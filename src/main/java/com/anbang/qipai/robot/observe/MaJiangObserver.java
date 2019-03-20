package com.anbang.qipai.robot.observe;

import com.anbang.qipai.robot.plan.bean.AnBangException;
import com.anbang.qipai.robot.plan.bean.TuoGuanInfo;
import com.anbang.qipai.robot.websocket.MajiangTuoGuanClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: 吴硕涵
 * @Date: 2019/3/4 11:23 AM
 * @Version 1.0
 */
//观察者 订阅者
public class MaJiangObserver implements Observer {
    Logger logger = LoggerFactory.getLogger(MaJiangObserver.class);

    private String playerId;  //就是memberId
    private String gameToken;
    private String gameId;
    private String wsUrl;
    private String gameType;

    public MaJiangObserver() {
    }

    public MaJiangObserver(String gameId, String wsUrl, String gameType) {
        this.gameId = gameId;
        TuoGuanInfo tuoGuan = AllGameMap.findTuoGuan(gameId);
        playerId = tuoGuan.getPlayerId();
        gameToken = tuoGuan.getToken();
        this.wsUrl = wsUrl;
        this.gameType = gameType;
    }


    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

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
        logger.info("托管了+" + playerId + " " + gameToken);
        try {
            if (StringUtils.isEmpty(playerId) || StringUtils.isEmpty(gameToken)) {
                return; //都不得为空
            }
            MajiangTuoGuanClient client = new MajiangTuoGuanClient(wsUrl, gameToken, gameId, playerId, gameType);
            AllGameMap.regisiterTuoGuan(gameId); //为正在托管的游戏队列加一个
            client.connect();

            //MajiangObserve在这里相当于一个分配线程 ，它并不直接参与到游戏到本身
        } catch (Exception e) {
            AllGameMap.deleteGame(gameId);//删除该局游戏的托管

            throw new AnBangException("托管客户端连接异常");
        }
    }


}
