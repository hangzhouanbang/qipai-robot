package com.anbang.qipai.robot.msg.receiver;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.robot.cqrs.c.domain.game.Game;
import com.anbang.qipai.robot.msg.channel.sink.FangpaoMajiangGameSink;
import com.anbang.qipai.robot.msg.msjobj.CommonMO;
import com.anbang.qipai.robot.plan.service.RobotService;
import com.google.gson.Gson;

@EnableBinding(FangpaoMajiangGameSink.class)
public class FangpaoMajiangGameMsgReceiver {

	@Autowired
	private RobotService robotService;

	private Gson gson = new Gson();

	@StreamListener(FangpaoMajiangGameSink.FANGPAOMAJIANGGAME)
	public void receive(CommonMO mo) {
		String msg = mo.getMsg();
		if ("playerQuit".equals(msg)) {// 有人退出游戏
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			String playerId = (String) data.get("playerId");
			robotService.tuoguanByPlayerLeaveGame(Game.fangpaoMajiang, gameId, playerId);
		}
		if ("new token".equals(msg)) {// 更新玩家token
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			String playerId = (String) data.get("playerId");
			String token = (String) data.get("token");
			robotService.updatePlayerToken(playerId, gameId, token);
		}
	}
}
