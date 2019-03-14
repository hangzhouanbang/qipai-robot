package com.anbang.qipai.robot.msg.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anbang.qipai.robot.msg.channel.sink.RuiAnGameRoomSink;
import com.anbang.qipai.robot.msg.service.RobotDistributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.util.StringUtils;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 11:43 AM
 * @Version 1.0
 */

@EnableBinding(RuiAnGameRoomSink.class)
public class RuiAnRoomMsgReceiver {
    Logger logger = LoggerFactory.getLogger(RuiAnRoomMsgReceiver.class);

    @Autowired
    private RobotDistributeService distributeService;

    @StreamListener(RuiAnGameRoomSink.CHANNEL)
    public void robotJoinRoom(String message) {
        logger.info("瑞安麻将接收到" + message);

        JSONObject object = JSON.parseObject(message);

        if (!object.getString("msg").equals("create gameroom")) {
            return;
        }

        logger.info("接收到的消息：" + object);

        String gameType = object.getJSONObject("data").getString("game");
        String gameId = object.getJSONObject("data").getString("gameId");

        if (StringUtils.isEmpty(gameType) || StringUtils.isEmpty(gameId)) {
            return;
        }

        distributeService.distributeRobot(gameType, gameId);

    }
}
