package com.anbang.qipai.robot.msg.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anbang.qipai.robot.msg.channel.sink.DoudizhuGameRoomSink;
import com.anbang.qipai.robot.msg.service.RobotDistributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.util.StringUtils;

@EnableBinding(DoudizhuGameRoomSink.class)
public class DoudizhuGameRoomMsgReceiver {

    @Autowired
    private RobotDistributeService distributeService;

    @StreamListener(DoudizhuGameRoomSink.CHANNEL)
    public void robotJoinRoom(String message) {

        JSONObject object = JSON.parseObject(message);

        if (!object.getString("msg").equals("create gameroom")) {
            return;
        }

        String gameType = object.getJSONObject("data").getString("game");
        String gameId = object.getJSONObject("data").getString("gameId");

        if (StringUtils.isEmpty(gameType) || StringUtils.isEmpty(gameId)) {
            return;
        }

        // TODO: 2019/4/2
        System.out.println("---------------gameType 创建房间");
        try {
            distributeService.distributeRobot(gameType, gameId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
