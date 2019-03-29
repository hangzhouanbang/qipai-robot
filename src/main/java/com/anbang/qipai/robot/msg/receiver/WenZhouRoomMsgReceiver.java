package com.anbang.qipai.robot.msg.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anbang.qipai.robot.msg.channel.sink.WenZhouGameRoomSink;
import com.anbang.qipai.robot.msg.service.RobotDistributeService;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:30 PM
 * @Version 1.0
 */

@EnableBinding(WenZhouGameRoomSink.class)
public class WenZhouRoomMsgReceiver {

	@Autowired
	private RobotDistributeService distributeService;

	@StreamListener(WenZhouGameRoomSink.CHANNEL)
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

		distributeService.distributeRobot(gameType, gameId);

	}
}
