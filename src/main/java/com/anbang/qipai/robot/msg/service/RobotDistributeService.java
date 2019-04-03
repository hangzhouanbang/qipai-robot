package com.anbang.qipai.robot.msg.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.anbang.qipai.robot.config.UrlConfig;
import com.anbang.qipai.robot.dao.dataObject.RobotMemberDbo;
import com.anbang.qipai.robot.exceptions.AnBangException;
import com.anbang.qipai.robot.model.Robots;
import com.anbang.qipai.robot.thread.ThreadPool;
import com.anbang.qipai.robot.thread.ThreadPoolFactory;
import com.anbang.qipai.robot.thread.task.JoinGameTask;
import com.anbang.qipai.robot.utils.HttpUtils;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 1:15 PM
 * @Version 1.0
 */
@Component
public class RobotDistributeService {

	public void distributeRobot(String gameType, String gameId) {

		String gameInfoUrl = null;
		if (gameType.equals("wenzhouShuangkou")) {
			gameInfoUrl = UrlConfig.getInfoWZSKUrl();
		} else if (gameType.equals("ruianMajiang")) {
			gameInfoUrl = UrlConfig.getInfoRAMJUrl();
		} else if (gameType.equals("dianpaoMajiang")) {
			gameInfoUrl = UrlConfig.getInfoDPMJUrl();
		} else if (gameType.equals("fangpaoMajiang")) {
			gameInfoUrl = UrlConfig.getInfoFPMJUrl();
		} else if (gameType.equals("wenzhouMajiang")) {
			gameInfoUrl = UrlConfig.getInfoWZMJUrl();
		} else if(gameType.equals("doudizhu")){
			gameInfoUrl = UrlConfig.getInfoDDZUrl();
		} else {
			return;
		}

		int size = 0; // 游戏还差几个人
		int renshu = 0;
		try {
			Map<String, String> map = new HashMap<>();
			map.put("gameId", gameId);
			String doPost = HttpUtils.doPost(gameInfoUrl, map);
			size = JSON.parseObject(doPost).getJSONObject("data").getJSONObject("game").getJSONArray("playerList")
					.size();
			String renshuString = JSON.parseObject(doPost).getJSONObject("data").getJSONObject("game")
					.getString("renshu");
			renshu = Integer.parseInt(renshuString);
		} catch (Exception e) {
			return;
		}

		if (renshu == 0) {
			return;
		}

		for (int i = 0; i < (renshu - size); i++) {
			synchronized (Robots.class) {
				Map<String, RobotMemberDbo> availableRobot = Robots.getAvailableRobot();
				Map<String, RobotMemberDbo> taskedRobot = Robots.getTaskedRobot();

				// 随机选一个机器人
				if (availableRobot.size() == 0) {
					throw new AnBangException("机器人不够啦");
				}

				Random random = new Random();
				int rand = random.nextInt(availableRobot.size());
				String[] keyArray = availableRobot.keySet().toArray(new String[0]);
				String key = keyArray[rand];
				RobotMemberDbo robot = availableRobot.get(key);

				// 增加任务队列 减少可用队列
				availableRobot.remove(key);
				taskedRobot.put(key, robot);
				Robots.setAvailableRobot(availableRobot);
				Robots.setTaskedRobot(taskedRobot);

				// int nextInt = random.nextInt(1500);
				// try {
				// synchronized (this) {
				// wait(1000 + nextInt);
				// }
				// } catch (Exception e) {
				// return;
				// }

				JoinGameTask task = new JoinGameTask(gameId, gameType, robot.getNickname(), robot.getGender(),
						robot.getHeadimgurl(), robot.getUnionid(), robot.getOpenid(), robot.getId());

				ThreadPool threadPool = ThreadPoolFactory.getThreadPool(gameType);

				threadPool.execute(task);
			}
		}
	}

}
