package com.anbang.qipai.robot.msg.receiver;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.robot.msg.channel.sink.DianpaoGameRoomSink;
import com.anbang.qipai.robot.msg.msjobj.CommonMO;
import com.anbang.qipai.robot.plan.service.RobotService;
import com.google.gson.Gson;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:28 PM
 * @Version 1.0
 */
@EnableBinding(DianpaoGameRoomSink.class)
public class DianpaoRoomMsgReceiver {

	@Autowired
	private RobotService robotService;

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private Gson gson = new Gson();

	@StreamListener(DianpaoGameRoomSink.DIANPAOGAMEROOM)
	public void joinGame(CommonMO mo) {
		String msg = mo.getMsg();
		if (msg.equals("create gameroom")) {
			Map data = (Map) mo.getData();
			if (data.get("game") != null && data.get("gameId") != null) {
				String game = (String) data.get("game");
				String gameId = (String) data.get("gameId");
				executorService.submit(() -> {
					robotService.distributeRobot(game, gameId);
				});
			}
		}
	}
}
