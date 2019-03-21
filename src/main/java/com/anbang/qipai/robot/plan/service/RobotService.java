package com.anbang.qipai.robot.plan.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.anbang.qipai.robot.cqrs.c.domain.RobotAlreadyActiveException;
import com.anbang.qipai.robot.cqrs.c.domain.RobotManager;
import com.anbang.qipai.robot.cqrs.c.domain.game.Game;
import com.anbang.qipai.robot.cqrs.q.dao.AuthorizationDboDao;
import com.anbang.qipai.robot.cqrs.q.dao.RobotMemberDboDao;
import com.anbang.qipai.robot.cqrs.q.dbo.AuthorizationDbo;
import com.anbang.qipai.robot.cqrs.q.dbo.RobotDbo;
import com.highto.framework.ddd.SingletonEntityRepository;

@Service
public class RobotService {

	@Autowired
	private RobotMemberDboDao robotMemberDboDao;

	@Autowired
	private AuthorizationDboDao authorizationDboDao;

	@Autowired
	private SingletonEntityRepository singletonEntityRepository;

	/**
	 * 所有机器人
	 */
	private Map<String, RobotDbo> allRobot = new ConcurrentHashMap<>();

	/**
	 * 活动中的机器人
	 */
	private Map<String, RobotDbo> activeRobot = new ConcurrentHashMap<>();

	public void distributeRobot(String game, String gameId) {
		if (allRobot.isEmpty()) {
			List<RobotDbo> robotList = robotMemberDboDao.findAll(1, 100);
			for (RobotDbo robot : robotList) {
				allRobot.put(robot.getId(), robot);
			}
		}
		RobotManager robotManager = singletonEntityRepository.getEntity(RobotManager.class);
		int joinNum = 0;
		for (RobotDbo robot : allRobot.values()) {
			if (!activeRobot.containsKey(robot.getId())) {
				AuthorizationDbo uniAuthDbo = authorizationDboDao.findAuthorizationDboByMemberIdAndPublisher(true,
						robot.getId(), "union.robot");
				AuthorizationDbo openAuthDbo = authorizationDboDao.findAuthorizationDboByMemberIdAndPublisher(true,
						robot.getId(), "open.robot.app.qipai");
				try {
					robotManager.joinGame(Game.valueOf(game), gameId, robot, uniAuthDbo.getUuid(),
							openAuthDbo.getUuid());
					activeRobot.put(robot.getId(), robot);
					joinNum++;
					if (joinNum == 3) {
						break;
					}
				} catch (RobotAlreadyActiveException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Scheduled(cron = "0 0/5 * * * ?") // 每5分刷新活跃机器人
	public void removeClosedRobot() {
		RobotManager robotManager = singletonEntityRepository.getEntity(RobotManager.class);
		List<String> activeRobotList = robotManager.removeClosedRobotClient();
		activeRobot.clear();
		for (String robotId : activeRobotList) {
			RobotDbo robot = allRobot.get(robotId);
			activeRobot.put(robot.getId(), robot);
		}
	}
}
