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
	private Map<String, RobotWebSocketClient> playerIdRobotActiveMap = new ConcurrentHashMap<>();

	private ExecutorService executorService = Executors.newCachedThreadPool();

	public void joinGame(Game game, String gameId, RobotDbo robotDbo, String unionid, String openid)
			throws RobotAlreadyActiveException {
		if (playerIdRobotActiveMap.containsKey(robotDbo.getId())) {
			throw new RobotAlreadyActiveException();
		}
		executorService.submit(() -> {
			try {
				Robot robot = generateRobotByGame(game, gameId, robotDbo, unionid, openid);
				RobotWebSocketClient robotClient = new RobotWebSocketClient(new URI(robot.getWsUrl()), robot);
				robot.setClient(robotClient);
				robotClient.connect();
				playerIdRobotActiveMap.put(robotDbo.getId(), robotClient);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public List<String> removeClosedRobotClient() {
		List<String> activeRobotList = new ArrayList<>();
		if (!playerIdRobotActiveMap.isEmpty()) {
			for (String robotId : playerIdRobotActiveMap.keySet()) {
				RobotWebSocketClient client = playerIdRobotActiveMap.get(robotId);
				if (client.isClosed()) {
					playerIdRobotActiveMap.remove(robotId);
				}
			}
		}
		activeRobotList.addAll(playerIdRobotActiveMap.keySet());
		return activeRobotList;
	}

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

	public Map<String, RobotWebSocketClient> getPlayerIdRobotActiveMap() {
		return playerIdRobotActiveMap;
	}

	public void setPlayerIdRobotActiveMap(Map<String, RobotWebSocketClient> playerIdRobotActiveMap) {
		this.playerIdRobotActiveMap = playerIdRobotActiveMap;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

}
