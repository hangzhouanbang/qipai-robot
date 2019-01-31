package com.anbang.fake.msg.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anbang.fake.model.CommonMO;
import com.anbang.fake.msg.channel.sink.RuiAnGameRoomSink;
import com.anbang.fake.msg.channel.sink.WenzhouShuangkouGameRoomSink;
import com.anbang.fake.msg.service.RobotDistributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.util.StringUtils;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/25 4:20 PM
 * @Version 1.0
 */

@EnableBinding(RuiAnGameRoomSink.class)
public class RuiANRoomMsgReceiver {
    Logger logger = LoggerFactory.getLogger(RuiANRoomMsgReceiver.class);

    @Autowired
    private RobotDistributeService distributeService;

    @StreamListener(RuiAnGameRoomSink.CHANNEL)
    public void robotJoinRoom(String message) {
        //TODO kafka接收
        int index = message.indexOf("msg");
        if (index == 0) {
            return;
        }
        String data = message.substring(index - 2, message.length());

        JSONObject object = JSON.parseObject(data);
        logger.info("接收到的消息：" + object);

        String gameType = object.getJSONObject("data").getString("game");
        String gameId = object.getJSONObject("data").getString("gameId");

        if (StringUtils.isEmpty(gameType) || StringUtils.isEmpty(gameId)) {
            return;
        }

        if (object.getString("msg").equals("create gameroom")) {
            distributeService.distributeRobot(gameType, gameId);
        }

    }
}
