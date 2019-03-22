package com.anbang.qipai.robot.plan.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.anbang.qipai.robot.cqrs.c.domain.RobotAlreadyActiveException;
import com.anbang.qipai.robot.cqrs.c.domain.RobotManager;
import com.anbang.qipai.robot.cqrs.c.domain.game.Game;
import com.anbang.qipai.robot.cqrs.q.dao.AuthorizationDboDao;
import com.anbang.qipai.robot.cqrs.q.dao.RobotDboDao;
import com.anbang.qipai.robot.cqrs.q.dbo.AuthorizationDbo;
import com.anbang.qipai.robot.cqrs.q.dbo.RobotDbo;
import com.anbang.qipai.robot.plan.bean.PlayerInfo;
import com.anbang.qipai.robot.plan.bean.RobotMemberDbo;
import com.anbang.qipai.robot.plan.dao.PlayerInfoDao;
import com.anbang.qipai.robot.plan.dao.RobotMemberDboDao;
import com.anbang.qipai.robot.remote.service.QipaiMembersRemoteService;
import com.highto.framework.ddd.SingletonEntityRepository;

@Service
public class RobotService {

	@Autowired
	private QipaiMembersRemoteService qipaiMembersRemoteService;

	@Autowired
	private RobotDboDao robotDboDao;

	@Autowired
	private RobotMemberDboDao robotMemberDboDao;

	@Autowired
	private PlayerInfoDao playerInfoDao;

	@Autowired
	private AuthorizationDboDao authorizationDboDao;

	@Autowired
	private SingletonEntityRepository singletonEntityRepository;

	public void init() {
		List<RobotMemberDbo> robotMemberDboList = robotMemberDboDao.findAll();
		RobotDbo robot = new RobotDbo();
		// robot.setNickname(nickname);
		// robot.setHeadimgurl(headimgurl);
		// robot.setGender(gender);
		robotDboDao.insert(robot);
		AuthorizationDbo unionAuthDbo = new AuthorizationDbo();
		authorizationDboDao.save(unionAuthDbo);
		AuthorizationDbo openAuthDbo = new AuthorizationDbo();
		authorizationDboDao.save(openAuthDbo);
	}

	/**
	 * 玩家id和gameId
	 */
	private Map<String, String> playerIdGameIdMap = new ConcurrentHashMap<>();

	/**
	 * 玩家id和token
	 */
	private Map<String, String> playerIdTokenMap = new ConcurrentHashMap<>();

	/**
	 * 所有机器人
	 */
	private Map<String, RobotDbo> allRobot = new ConcurrentHashMap<>();

	/**
	 * 活动中的机器人
	 */
	private Map<String, RobotDbo> activeRobot = new ConcurrentHashMap<>();

	/**
	 * 更新玩家token，玩家与gameId和token之间存在一对多关系，但玩家同一时间只能进行一场游戏，所以可以简单认为一对一
	 */
	public void updatePlayerToken(String playerId, String gameId, String token) {
		playerIdGameIdMap.put(playerId, gameId);
		playerIdTokenMap.put(playerId, token);
	}

	/**
	 * 玩家离线托管
	 */
	public void tuoguanByPlayerLeaveGame(Game game, String gameId, String playerId) {
		String token = playerIdTokenMap.get(playerId);
		if (StringUtil.isBlank(token)) {
			return;
		}
		PlayerInfo playerInfo = playerInfoDao.findById(playerId);
		RobotManager robotManager = singletonEntityRepository.getEntity(RobotManager.class);
		try {
			robotManager.tuoguan(game, gameId, playerId, playerInfo.getNickname(), playerInfo.getHeadimgurl(),
					playerInfo.getGender(), token);
		} catch (RobotAlreadyActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 玩家主动托管
	 */
	public void tuoguanByPlayerId(Game game, String gameId, String playerId) throws RobotAlreadyActiveException {
		String token = playerIdTokenMap.get(playerId);
		if (StringUtil.isBlank(token)) {
			return;
		}
		PlayerInfo playerInfo = playerInfoDao.findById(playerId);
		RobotManager robotManager = singletonEntityRepository.getEntity(RobotManager.class);
		robotManager.tuoguan(game, gameId, playerId, playerInfo.getNickname(), playerInfo.getHeadimgurl(),
				playerInfo.getGender(), token);
	}

	/**
	 * 玩家主动取消托管
	 */
	public void cancelTuoguanByPlayerId(String gameId, String playerId) {
		RobotManager robotManager = singletonEntityRepository.getEntity(RobotManager.class);
		robotManager.cancelTuoguanByPlayerId(gameId, playerId);
	}

	/**
	 * 分配机器人加入游戏
	 */
	public void distributeRobot(String game, String gameId) {
		if (allRobot.isEmpty()) {
			List<RobotDbo> robotList = robotDboDao.findAll(1, 100);
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

	@Scheduled(cron = "0 0 6 * * ?") // 每天早上6点刷新活跃机器人
	public void removeClosedTuoguanRobot() {
		RobotManager robotManager = singletonEntityRepository.getEntity(RobotManager.class);
		robotManager.removeClosedTuoguanClient();
	}
}
