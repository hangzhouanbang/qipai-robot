package com.anbang.qipai.robot.cqrs.c.domain;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.ClientProtocolException;

import com.anbang.qipai.robot.cqrs.c.domain.dianpaomajiang.DianpaoMajiangRobot;
import com.anbang.qipai.robot.cqrs.c.domain.fangpaomajiang.FangpaoMajiangRobot;
import com.anbang.qipai.robot.cqrs.c.domain.game.Game;
import com.anbang.qipai.robot.cqrs.c.domain.ruianmajiang.RuianMajiangRobot;
import com.anbang.qipai.robot.cqrs.c.domain.wenzhoumajiang.WenzhouMajiangRobot;
import com.anbang.qipai.robot.cqrs.c.domain.wenzhoushuangkou.WenzhouShuangkouRobot;
import com.anbang.qipai.robot.cqrs.q.dbo.RobotDbo;

/**
 * 机器人管理
 * 
 * @author lsc
 *
 */
public class RobotManager {

	/**
	 * 活动中的机器人
	 */
	private Map<String, RobotWebSocketClient> robotIdRobotActiveMap = new ConcurrentHashMap<>();

	/**
	 * 玩家在某长游戏对应的机器人
	 */
	private Map<String, RobotWebSocketClient> playerIdGameIdRobotActiveMap = new ConcurrentHashMap<>();

	private ExecutorService executorService = Executors.newCachedThreadPool();

	/**
	 * 机器人加入游戏
	 */
	public void joinGame(Game game, String gameId, RobotDbo robotDbo, String unionid, String openid)
			throws RobotAlreadyActiveException {
		if (robotIdRobotActiveMap.containsKey(robotDbo.getId())) {
			throw new RobotAlreadyActiveException();
		}
		executorService.submit(() -> {
			try {
				Robot robot = generateRobotByGame(game, gameId, robotDbo, unionid, openid);
				RobotWebSocketClient robotClient = new RobotWebSocketClient(new URI(robot.getWsUrl()), robot);
				robot.setClient(robotClient);
				robotClient.connect();
				robotIdRobotActiveMap.put(robotDbo.getId(), robotClient);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 移除已经关闭的机器人
	 */
	public List<String> removeClosedRobotClient() {
		List<String> activeRobotList = new ArrayList<>();
		if (!robotIdRobotActiveMap.isEmpty()) {
			for (String robotId : robotIdRobotActiveMap.keySet()) {
				RobotWebSocketClient client = robotIdRobotActiveMap.get(robotId);
				if (client.isClosed()) {
					robotIdRobotActiveMap.remove(robotId);
				}
			}
		}
		activeRobotList.addAll(robotIdRobotActiveMap.keySet());
		return activeRobotList;
	}

	/**
	 * 根据游戏创建机器人
	 */
	private Robot generateRobotByGame(Game game, String gameId, RobotDbo robotDbo, String unionid, String openid)
			throws ClientProtocolException, IOException, RobotLoginException, JoinGameException {
		if (game.equals(Game.ruianMajiang)) {
			return new RuianMajiangRobot(game, gameId, robotDbo, unionid, openid);
		} else if (game.equals(Game.wenzhouMajiang)) {
			return new WenzhouMajiangRobot(game, gameId, robotDbo, unionid, openid);
		} else if (game.equals(Game.fangpaoMajiang)) {
			return new FangpaoMajiangRobot(game, gameId, robotDbo, unionid, openid);
		} else if (game.equals(Game.dianpaoMajiang)) {
			return new DianpaoMajiangRobot(game, gameId, robotDbo, unionid, openid);
		} else if (game.equals(Game.wenzhouShuangkou)) {
			return new WenzhouShuangkouRobot(game, gameId, robotDbo, unionid, openid);
		}
		return null;
	}

	/**
	 * 玩家托管
	 */
	public void tuoguan(Game game, String gameId, String playerId, String nickname, String headimgurl, String gender,
			String token) throws RobotAlreadyActiveException {
		if (playerIdGameIdRobotActiveMap.containsKey(playerId + gameId)) {
			throw new RobotAlreadyActiveException();
		}
		executorService.submit(() -> {
			try {
				Robot robot = distributeRobotByGame(game, gameId, playerId, nickname, headimgurl, gender, token);
				RobotWebSocketClient robotClient = new RobotWebSocketClient(new URI(robot.getWsUrl()), robot);
				robot.setClient(robotClient);
				robotClient.connect();
				playerIdGameIdRobotActiveMap.put(playerId + gameId, robotClient);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 取消玩家托管
	 */
	public void cancelTuoguanByPlayerId(String gameId, String playerId) {
		RobotWebSocketClient robotClient = playerIdGameIdRobotActiveMap.remove(playerId + gameId);
		if (robotClient != null) {
			robotClient.doClose();
		}
	}

	/**
	 * 移除所有已经关闭的托管服务
	 */
	public void removeClosedTuoguanClient() {
		if (!playerIdGameIdRobotActiveMap.isEmpty()) {
			for (String playerGameId : playerIdGameIdRobotActiveMap.keySet()) {
				RobotWebSocketClient client = playerIdGameIdRobotActiveMap.get(playerGameId);
				if (client.isClosed()) {
					playerIdGameIdRobotActiveMap.remove(playerGameId);
				}
			}
		}
	}

	/**
	 * 根据游戏分配机器人
	 */
	private Robot distributeRobotByGame(Game game, String gameId, String playerId, String nickname, String headimgurl,
			String gender, String token) {
		if (game.equals(Game.ruianMajiang)) {
			return new RuianMajiangRobot(game, gameId, playerId, nickname, headimgurl, gender, token);
		} else if (game.equals(Game.wenzhouMajiang)) {
			return new WenzhouMajiangRobot(game, gameId, playerId, nickname, headimgurl, gender, token);
		} else if (game.equals(Game.fangpaoMajiang)) {
			return new FangpaoMajiangRobot(game, gameId, playerId, nickname, headimgurl, gender, token);
		} else if (game.equals(Game.dianpaoMajiang)) {
			return new DianpaoMajiangRobot(game, gameId, playerId, nickname, headimgurl, gender, token);
		} else if (game.equals(Game.wenzhouShuangkou)) {
			return new WenzhouShuangkouRobot(game, gameId, playerId, nickname, headimgurl, gender, token);
		}
		return null;
	}

	public Map<String, RobotWebSocketClient> getPlayerIdGameIdRobotActiveMap() {
		return playerIdGameIdRobotActiveMap;
	}

	public void setPlayerIdGameIdRobotActiveMap(Map<String, RobotWebSocketClient> playerIdGameIdRobotActiveMap) {
		this.playerIdGameIdRobotActiveMap = playerIdGameIdRobotActiveMap;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public Map<String, RobotWebSocketClient> getRobotIdRobotActiveMap() {
		return robotIdRobotActiveMap;
	}

	public void setRobotIdRobotActiveMap(Map<String, RobotWebSocketClient> robotIdRobotActiveMap) {
		this.robotIdRobotActiveMap = robotIdRobotActiveMap;
	}

}
