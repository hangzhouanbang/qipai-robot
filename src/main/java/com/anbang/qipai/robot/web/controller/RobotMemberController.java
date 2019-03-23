package com.anbang.qipai.robot.web.controller;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anbang.qipai.robot.cqrs.q.dbo.RobotDbo;
import com.anbang.qipai.robot.cqrs.q.service.MemberAuthQueryService;
import com.anbang.qipai.robot.remote.service.QipaiXiuxianchangRemoteService;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:16 PM
 * @Version 1.0
 */

@RestController
@RequestMapping("/robot")
public class RobotMemberController {

	@Autowired
	private MemberAuthQueryService memberAuthQueryService;

	@Autowired
	private QipaiXiuxianchangRemoteService qipaiXiuxianchangRemoteService;

	/**
	 * 给机器人增加不同数量金币
	 */
	@RequestMapping("/addgold")
	public void addgold() {
		Random random = new Random();
		int size = 20;
		long count = memberAuthQueryService.countRobotAmount();
		long pageCount = count % size > 0 ? count / size + 1 : count / size;
		for (int page = 1; page <= pageCount; page++) {
			int nextInt = random.nextInt(100);
			int amount = nextInt + 300;
			List<RobotDbo> robotList = memberAuthQueryService.findAllRobots(page, size);
			String[] memberIds = new String[robotList.size()];
			for (int i = 0; i < robotList.size(); i++) {
				RobotDbo robot = robotList.get(i);
				memberIds[i] = robot.getId();
			}
			qipaiXiuxianchangRemoteService.gold_givegoldtomembers(memberIds, amount, "give gold to robot");
		}
	}

	/**
	 * 给机器人增加相同数量金币
	 */
	@RequestMapping("/addsamegold")
	public void addSameGold() {
		int size = 2000;
		long count = memberAuthQueryService.countRobotAmount();
		long pageCount = count % size > 0 ? count / size + 1 : count / size;
		for (int page = 1; page <= pageCount; page++) {
			int amount = 1000;
			List<RobotDbo> robotList = memberAuthQueryService.findAllRobots(page, size);
			String[] memberIds = new String[robotList.size()];
			for (int i = 0; i < robotList.size(); i++) {
				RobotDbo robot = robotList.get(i);
				memberIds[i] = robot.getId();
			}
			qipaiXiuxianchangRemoteService.gold_givegoldtomembers(memberIds, amount, "give gold to robot");
		}
	}

}
