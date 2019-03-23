package com.anbang.qipai.robot.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anbang.qipai.robot.cqrs.c.domain.RobotAlreadyActiveException;
import com.anbang.qipai.robot.cqrs.c.domain.game.Game;
import com.anbang.qipai.robot.plan.service.MemberAuthService;
import com.anbang.qipai.robot.plan.service.RobotService;
import com.anbang.qipai.robot.web.vo.CommonVO;

@RestController
@RequestMapping("/tuoguan")
public class TuoguanController {
	@Autowired
	private MemberAuthService memberAuthService;

	@Autowired
	private RobotService robotService;

	@RequestMapping("/register")
	public CommonVO registerRobot() {
		CommonVO vo = new CommonVO();
		try {
			robotService.init();
		} catch (Exception e) {
			vo.setSuccess(false);
			vo.setMsg(e.getClass().getName());
			return vo;
		}
		return vo;
	}

	@RequestMapping("/tuoguan")
	public CommonVO tuoguan(Game game, String gameId, String token) {
		CommonVO vo = new CommonVO();
		String playerId = memberAuthService.getMemberIdBySessionId(token);
		if (playerId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		try {
			robotService.tuoguanByPlayerId(game, gameId, playerId);
		} catch (RobotAlreadyActiveException e) {
			vo.setSuccess(false);
			vo.setMsg("RobotAlreadyActiveException");
			return vo;
		}
		return vo;
	}

	@RequestMapping("/cancel")
	public CommonVO cancelTuoguan(String gameId, String token) {
		CommonVO vo = new CommonVO();
		String playerId = memberAuthService.getMemberIdBySessionId(token);
		if (playerId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		robotService.cancelTuoguanByPlayerId(gameId, playerId);
		return vo;
	}
}
