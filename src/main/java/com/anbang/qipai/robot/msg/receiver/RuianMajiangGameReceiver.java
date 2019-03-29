package com.anbang.qipai.robot.msg.receiver;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anbang.qipai.robot.msg.channel.sink.tuoguan.RuianMajiangGame;
import com.anbang.qipai.robot.observe.AllGameMap;

/**
 * @Author: 吴硕涵
 * @Date: 2019/3/4 2:35 PM
 * @Version 1.0
 */
@EnableBinding(RuianMajiangGame.class)
public class RuianMajiangGameReceiver {

	@StreamListener(RuianMajiangGame.CHANNEL)
	public void tuoGuan(String message) {

		JSONObject jsonObject = JSON.parseObject(message);

		if (jsonObject.getString("msg").equals("new token")) {
			String gameId = jsonObject.getJSONObject("data").getString("gameId");
			String playerId = jsonObject.getJSONObject("data").getString("playerId");
			String gameToken = jsonObject.getJSONObject("data").getString("token");
			AllGameMap.add(gameId, playerId, gameToken);
		}
	}
}
