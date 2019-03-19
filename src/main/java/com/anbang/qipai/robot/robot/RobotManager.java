package com.anbang.qipai.robot.robot;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.ClientProtocolException;

import com.anbang.qipai.robot.dao.dataObject.RobotMemberDbo;
import com.anbang.qipai.robot.robot.dianpaomajiang.DianpaoMajiangRobot;
import com.anbang.qipai.robot.robot.fangpaomajiang.FangpaoMajiangRobot;
import com.anbang.qipai.robot.robot.game.Game;
import com.anbang.qipai.robot.robot.ruianmajiang.RuianMajiangRobot;
import com.anbang.qipai.robot.robot.websocket.RobotWebSocketClient;
import com.anbang.qipai.robot.robot.wenzhoumajiang.WenzhouMajiangRobot;
import com.anbang.qipai.robot.robot.wenzhoushuangkou.WenzhouShuangkouRobot;

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

	public void joinGame(Game game, String gameId, RobotMemberDbo robotDbo) {
		executorService.submit(() -> {
			try {
				Robot robot = generateRobotByGame(game, gameId, robotDbo);
				RobotWebSocketClient robotClient = new RobotWebSocketClient(new URI(robot.getWsUrl()), robot);
				robot.setClient(robotClient);
				playerIdRobotActiveMap.put(robotDbo.getId(), robotClient);
				robotClient.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private Robot generateRobotByGame(Game game, String gameId, RobotMemberDbo robotDbo)
			throws ClientProtocolException, IOException {
		if (game.equals(Game.ruianMajiang)) {
			return new RuianMajiangRobot(game, gameId, robotDbo);
		} else if (game.equals(Game.wenzhouMajiang)) {
			return new WenzhouMajiangRobot(game, gameId, robotDbo);
		} else if (game.equals(Game.fangpaoMajiang)) {
			return new FangpaoMajiangRobot(game, gameId, robotDbo);
		} else if (game.equals(Game.dianpaoMajiang)) {
			return new DianpaoMajiangRobot(game, gameId, robotDbo);
		} else if (game.equals(Game.wenzhouShuangkou)) {
			return new WenzhouShuangkouRobot(game, gameId, robotDbo);
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
