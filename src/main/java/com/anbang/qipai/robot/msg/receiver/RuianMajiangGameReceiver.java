package com.anbang.qipai.robot.msg.receiver;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anbang.qipai.robot.msg.channel.sink.tuoguan.RuianMajiangGame;
import com.anbang.qipai.robot.observe.AllGameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

/**
 * @Author: 吴硕涵
 * @Date: 2019/3/4 2:35 PM
 * @Version 1.0
 */
@EnableBinding(RuianMajiangGame.class)
public class RuianMajiangGameReceiver {
    Logger logger = LoggerFactory.getLogger(RuianMajiangGameReceiver.class);

    @StreamListener(RuianMajiangGame.CHANNEL)
    public void tuoGuan(String message) {
//        logger.info("托管收到消息" + message);
        JSONObject jsonObject = JSON.parseObject(message);

        if (jsonObject.getString("msg").equals("new token")) {
            String gameId = jsonObject.getJSONObject("data").getString("gameId");
            String playerId = jsonObject.getJSONObject("data").getString("playerId");
            String gameToken = jsonObject.getJSONObject("data").getString("token");
            AllGameMap.add(gameId,playerId,gameToken);
        }
    }
}
