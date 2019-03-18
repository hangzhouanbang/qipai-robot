package com.anbang.qipai.robot.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anbang.qipai.robot.config.MajiangUrlConfig;
import com.anbang.qipai.robot.dao.dataObject.RobotMemberDbo;
import com.anbang.qipai.robot.exceptions.AnBangException;
import com.anbang.qipai.robot.model.CommonModel;
import com.anbang.qipai.robot.model.MajiangPlayerAction;
import com.anbang.qipai.robot.model.Robots;
import com.anbang.qipai.robot.observe.AllGameMap;
import com.anbang.qipai.robot.observe.MaJiangObserver;
import com.anbang.qipai.robot.utils.HttpUtils;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 1:11 PM
 * @Version 1.0
 */

// client内置线程 timetask线程(心跳) 可用队列回收
public class MajiangRobotClient extends WebSocketClient {

	Logger logger = LoggerFactory.getLogger(MajiangRobotClient.class);

	private String robotId;
	private String wsUrl;
	private String name;
	private String gameToken;
	private String gameId;
	private String memberId;
	private String gameType;

	private Map<String, String> configMap;

	private long readyTime;
	private long lastChuPaiTime;
	private boolean hasReady;

	private boolean terminate = false;

	private Map<String, String> gameMap;

	public MajiangRobotClient(String url, String robotName, String gameToken, String gameId, String robotId,
			String memberId, String gameType) throws URISyntaxException {
		super(new URI(url));
		this.wsUrl = url;
		this.name = robotName;
		this.gameToken = gameToken;
		this.gameId = gameId;
		this.memberId = memberId;

		gameMap = new HashMap<>();
		gameMap.put("token", gameToken);
		gameMap.put("gameId", gameId);
		this.robotId = robotId;

		this.hasReady = false;
		this.lastChuPaiTime = System.currentTimeMillis();

		this.gameType = gameType;
		if (gameType.equals("wenzhouMajiang")) {
			configMap = MajiangUrlConfig.getWenzhouMajiang();
		} else if (gameType.equals("ruianMajiang")) {
			configMap = MajiangUrlConfig.getRuianMajiang();
		} else if (gameType.equals("fangpaoMajiang")) {
			configMap = MajiangUrlConfig.getFangpaoMajiang();
		} else if (gameType.equals("dianpaoMajiang")) {
			configMap = MajiangUrlConfig.getDianpaoMajiang();
		}

		logger.info(name + "游戏类型" + gameType);
	}

	@Override
	public void onMessage(String paramString) {
		logger.info("机器人" + name + "socket接收到消息：" + paramString);
		JSONObject messageObject = JSON.parseObject(paramString);

		// try {

		if (messageObject.getString("msg").equals("bindPlayer")) {
			bindPlayer();
			// 绑定成功 则准备

		} else if (messageObject.getString("msg").equals("query")) {
			// 查状态
			if (messageObject.getJSONObject("data").getString("scope").equals("maidiState")) {
				hasReady = false;
				maidi();
			} else if (messageObject.getJSONObject("data").getString("scope").equals("panForMe")) {
				hasReady = false;
				panForMe();
			} else if (messageObject.getJSONObject("data").getString("scope").equals("panResult")) {
				readyToNextGame(); // 准备下一盘
			} else if (messageObject.getJSONObject("data").getString("scope").equals("juResult")) {
				huishou();
			} else if (messageObject.getJSONObject("data").getString("scope").equals("gameFinishVote")) {
				gameFinishVote();

				if (queryVote()) {
					// 机器人回收
					huishou();
				}
			}

		}
		// } catch (Exception e) {
		//
		// //只要出了问题 直接回收 + 关闭所有相关线程
		// terminate = true; //心跳关闭
		// notifyAndRefresh();//父线程唤醒
		// huishou();//机器人回收
		// close();//client关闭
		// throw new AnBangException(e);
		// }
	}

	// private void notifyAndRefresh() {
	//// 唤醒
	// ConditionPool.reentrantLockMap.get(fatherConditionId).lock();
	// ConditionPool.conditionMap.get(fatherConditionId).signalAll();
	//
	// ConditionPool.removeCondition(fatherConditionId);
	// ConditionPool.removeLock(fatherConditionId);
	// }

	@Override
	public void onOpen(ServerHandshake shake) {
		logger.info("机器人" + name + "建立三次握手");
		timer();
		logger.info("机器人" + name + "定时任务开始");
		logger.info("机器人" + name + "memberId" + memberId);

	}

	@Override
	public void onClose(int paramInt, String paramString, boolean paramBoolean) {
		logger.info("机器人" + name + "关闭...");
	}

	@Override
	public void onError(Exception e) {
		e.printStackTrace();
		throw new AnBangException(name + "Client遇到异常");
	}

	private void bindPlayer() {
		CommonModel model = new CommonModel();
		model.setMsg("bindPlayer");
		model.setData(gameMap);
		send(JSON.toJSONString(model));
		logger.info(name + "绑定玩家成功");
		ready(gameToken);
	}

	private void ready(String token) {
		Map<String, String> map = new HashMap<>();
		map.put("token", token);

		try {

			Random random = new Random();
			int rand = random.nextInt(1500);
			// 等待150ms准备

			synchronized (this) {
				wait(1000 + rand);
			}

			String doPost = HttpUtils.doPost(configMap.get("ready"), map);
			logger.info("机器人" + name + "请求ready的结果" + doPost);

			hasReady = true; // 已经准备
			readyTime = System.currentTimeMillis();

			if (JSON.parseObject(doPost).getJSONObject("data").getJSONArray("queryScopes").toJSONString()
					.contains("maidiState")) {
				maidi();
			} else if (JSON.parseObject(doPost).getJSONObject("data").getJSONArray("queryScopes")
					.contains("panForMe")) {
				panForMe();
			}

		} catch (Exception e) {
			huishou();
			throw new AnBangException(name + "ready函数异常");
		}

	}

	private void maidi() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("gameId", gameId);

		Map<String, String> maidiMap = new HashMap<>();
		maidiMap.put("token", gameToken);
		maidiMap.put("yes", "true");
		try {
			String maidiInfo = HttpUtils.doPost(configMap.get("maidiInfo"), infoMap);
			logger.info("买底的信息" + maidiInfo);

			if (!JSON.parseObject(maidiInfo).getString("success").equals("true")) {
				return;
			}

			String maidiState = JSON.parseObject(maidiInfo).getJSONObject("data").getJSONObject("maidiState")
					.getString(memberId);

			if (StringUtils.isEmpty(maidiState)) {
				return;
			}

			if (maidiState.equals("waitForMaidi") || maidiState.equals("startDingdi")
					|| maidiState.equals("startMaidi")) {

				String maidi = HttpUtils.doPost(configMap.get("maidi"), maidiMap);

				logger.info(name + "买底" + maidi);
				if (JSON.parseObject(maidi).getString("success").equals("true")) {
					if (JSON.parseObject(maidi).getJSONObject("data").getJSONArray("queryScopes")
							.contains("panForMe")) {
						panForMe();
					}
				}
				logger.info(name + "买底成功");
			}
		} catch (Exception e) {
			huishou();
			throw new AnBangException(name + "买底时异常");
		}
	}

	private void panForMe() {
		try {
			// logger.info(name + "请求panforme");
			String doPost = HttpUtils.doPost(configMap.get("panforme"), gameMap);
			// logger.info(name + "请求panForMe的结果" + doPost);
			JSONObject jsonObject = JSONObject.parseObject(doPost);
			if (jsonObject.getString("success").equals("false")) {
				return;
			}

			JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject("panActionFrame")
					.getJSONObject("panAfterAction").getJSONArray("playerList");

			int no = Integer.parseInt(jsonObject.getJSONObject("data").getJSONObject("panActionFrame").getString("no"))
					+ 1;

			for (int index = 0; index < jsonArray.size(); index++) {
				if (jsonArray.getJSONObject(index).getString("id").equals(memberId)) {
					action(jsonArray.getJSONObject(index), no); // 到自己了
					break;
				}
			}

		} catch (Exception e) {
			huishou();
			throw new AnBangException(name + "请求panforme异常");
		}

	}

	private void action(JSONObject jsonObject, int no) {
		// logger.info(name + "可以做的动作" + jsonObject.toJSONString());
		JSONArray actionCandidates = jsonObject.getJSONArray("actionCandidates");
		if (actionCandidates == null) {
			return;
		}
		if (actionCandidates.size() == 0) {
			return;
		}

		// 摸是单独的
		if (actionCandidates.toJSONString().contains("mo")) {
			String id = actionCandidates.getJSONObject(0).getString("id");
			postAction(id, no);
		} else {

			// 1、胡 2、杠 3、碰 4、吃 5、打 6、过
			MajiangPlayerAction[] playerActions = new MajiangPlayerAction[6];
			for (int index = 0; index < actionCandidates.size(); index++) {
				String type = actionCandidates.getJSONObject(index).getString("type");
				if (type.equals("hu")) {
					MajiangPlayerAction action = new MajiangPlayerAction();
					action.setId(actionCandidates.getJSONObject(index).getString("id"));
					action.setType("hu");
					playerActions[0] = action;
				} else if (type.equals("gang")) {
					MajiangPlayerAction action = new MajiangPlayerAction();
					action.setId(actionCandidates.getJSONObject(index).getString("id"));
					action.setType("gang");
					playerActions[1] = action;
				} else if (type.equals("peng")) {
					MajiangPlayerAction action = new MajiangPlayerAction();
					action.setId(actionCandidates.getJSONObject(index).getString("id"));
					action.setType("peng");
					playerActions[2] = action;
				} else if (type.equals("chi")) {
					MajiangPlayerAction action = new MajiangPlayerAction();
					action.setId(actionCandidates.getJSONObject(index).getString("id"));
					action.setType("chi");
					playerActions[3] = action;
				} else if (type.equals("da")) {
					if (playerActions[4] == null) {
						MajiangPlayerAction action = new MajiangPlayerAction();
						action.add(actionCandidates.getJSONObject(index).getString("id"),
								actionCandidates.getJSONObject(index).getString("pai"));
						playerActions[4] = action;
					} else {
						playerActions[4].add(actionCandidates.getJSONObject(index).getString("id"),
								actionCandidates.getJSONObject(index).getString("pai"));
					}
				} else if (type.equals("guo")) {
					MajiangPlayerAction action = new MajiangPlayerAction();
					action.setId(actionCandidates.getJSONObject(index).getString("id"));
					action.setType("guo");
					playerActions[5] = action;
				}
			}

			// 找优先级高的先打
			for (int index = 0; index < 6; index++) {
				if (playerActions[index] != null) {
					try {
						Random random = new Random();
						int nextInt = random.nextInt(2000);
						synchronized (this) {
							wait(1000 + nextInt);
						}
					} catch (Exception e) {
					}
					logger.info(name + "执行动作" + playerActions[index].getType());
					postAction(playerActions[index].getId(), no);
					break;
				}
			}
		}

	}

	private void postAction(String actionId, int actionNo) {
		Map<String, String> map = new HashMap<>();
		map.put("token", gameToken);
		map.put("id", actionId);
		map.put("actionNo", String.valueOf(actionNo));
		logger.info(name + "发送action请求" + map.toString());
		try {

			String action = HttpUtils.doPost(configMap.get("action"), map);
			logger.info(name + "请求postAction结果" + action);

			lastChuPaiTime = System.currentTimeMillis();

			if (JSON.parseObject(action).getJSONObject("data").getJSONArray("queryScopes").contains("panForMe")) {
				panForMe();
			}
			if (JSON.parseObject(action).getJSONObject("data").getJSONArray("queryScopes").contains("panResult")) {
				readyToNextGame();
			}
		} catch (Exception e) {
			huishou();
			throw new AnBangException(name + "请求action异常");
		}
	}

	// 线程级别的定时任务
	public void timer() {
		// Calendar c = Calendar.getInstance();
		// c.set(Calendar.HOUR_OF_DAY, 10); // 控制时
		// c.set(Calendar.MINUTE, 0); // 控制分
		// c.set(Calendar.SECOND, 0); // 控制秒
		//
		// Date time = c.getTime(); // 得到执行任务的时间,此处为当天的10：00：00

		ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!terminate) {
					sendHeartBeat();
				} else {
					logger.info("机器人" + name + "心跳结束");
					System.gc();
					Thread.currentThread().stop();
				}
			}

		}, 100, 7000, TimeUnit.MILLISECONDS);// 这里设定将延时每隔1000毫秒执行一次

	}

	public void sendHeartBeat() {
		// 发送心跳的时候如果10秒内不准备 机器人自己离开房间
		if (hasReady) {
			if (System.currentTimeMillis() - readyTime > 15 * 1000) {
				huishou();
				leaveRoom();
			}
		}

		// 托管启动了
		if (System.currentTimeMillis() - lastChuPaiTime > 15 * 1000 && (!AllGameMap.hasRegister(gameId))) {
			if (AllGameMap.containsGame(gameId)) {
				int remain = AllGameMap.removeOne(gameId, memberId);
				logger.info("托管启动了" + JSON.toJSONString(AllGameMap.allGameMap));
				if (remain == 1) { // 如果大家都发现他掉线了 就托管
					logger.info("准备托管");
					MaJiangObserver observer = new MaJiangObserver(gameId, wsUrl, gameType);
					observer.tuoguan();
				}
			}
		}

		// 如果一分钟不出牌 自己离开房间
		if (System.currentTimeMillis() - lastChuPaiTime > 300 * 1000) {
			huishou();
			leaveRoom();
		}

		if (!StringUtils.isEmpty(wsUrl)) {
			CommonModel model = new CommonModel();
			model.setMsg("heartbeat");
			Map<String, String> map = new HashMap<>();
			map.put("token", gameToken);
			model.setData(map);
			send(JSON.toJSONString(model));
			// logger.info("机器人" + name + "发送心跳");
		}
	}

	// TODO:
	private void faQiJieSan() {
		Map<String, String> map = new HashMap<>();
		map.put("token", gameToken);
		try {
			String doPost = HttpUtils.doPost(configMap.get("finish"), map);
			logger.info(name + "发起解散" + doPost);
		} catch (Exception e) {
			throw new AnBangException(name + "发起解散异常");
		}
	}

	private void leaveRoom() {
		Map<String, String> map = new HashMap<>();
		map.put("token", gameToken);
		try {
			String doPost = HttpUtils.doPost(configMap.get("leaveGame"), map);
			logger.info(name + "离开游戏" + doPost);
		} catch (Exception e) {
			throw new AnBangException(name + "离开游戏异常");
		}
	}

	private void readyToNextGame() {
		Map<String, String> map = new HashMap<>();
		map.put("token", gameToken);
		try {
			String doPost = HttpUtils.doPost(configMap.get("readyNext"), map);
			logger.info(name + "准备第二盘" + doPost);
		} catch (Exception e) {
			huishou();
			throw new AnBangException(name + "准备第二盘异常");
		}

	}

	private void gameFinishVote() {
		Map<String, String> map = new HashMap<>();
		map.put("token", gameToken);
		map.put("yes", "true");
		try {
			synchronized (this) {
				wait(1000);
			}
			String doPost = HttpUtils.doPost(configMap.get("vote"), map);
			logger.info(name + "投票请求" + doPost);
		} catch (Exception e) {
			huishou();
			throw new AnBangException(name + "游戏结束投票异常");
		}

	}

	private boolean queryVote() {

		try {
			synchronized (this) {
				wait(2500);
			}
			Map<String, String> map = new HashMap<>();
			map.put("gameId", gameId);
			String doPost = HttpUtils.doPost(configMap.get("queryVote"), map);
			logger.info(name + "查询投票结果" + doPost);

			JSONObject jsonObject = JSON.parseObject(doPost);
			String result = jsonObject.getJSONObject("data").getJSONObject("vote").getString("result");

			if (StringUtils.isEmpty(result)) {
				return true;
			}
			if (result.equals("yes")) {
				return true;
			}

			return false;

		} catch (Exception e) {
			huishou();
			throw new AnBangException(name + "查询投票结果异常");
		}
	}

	private void huishou() {
		terminate = true; // 心跳关闭
		synchronized (Robots.class) {
			Map<String, RobotMemberDbo> taskedRobot = Robots.getTaskedRobot();
			Map<String, RobotMemberDbo> availableRobot = Robots.getAvailableRobot();
			RobotMemberDbo memberDbo = taskedRobot.get(robotId);

			availableRobot.put(robotId, memberDbo);
			logger.info(memberDbo.getNickname() + "加入可用队列");
			taskedRobot.remove(robotId);
		}
		AllGameMap.deleteGame(gameId);
		close();// client关闭 最后关闭client
	}

}
