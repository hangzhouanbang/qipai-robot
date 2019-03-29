package com.anbang.qipai.robot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.anbang.qipai.robot.config.UrlConfig;
import com.anbang.qipai.robot.dao.dataObject.RobotMemberDbo;
import com.anbang.qipai.robot.exceptions.AnBangException;
import com.anbang.qipai.robot.model.BaseResult;
import com.anbang.qipai.robot.model.Robots;
import com.anbang.qipai.robot.service.RobotMemberService;
import com.anbang.qipai.robot.utils.HttpUtils;

import io.swagger.annotations.ApiOperation;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:16 PM
 * @Version 1.0
 */

// @CrossOrigin
@RestController
@RequestMapping("/member")
public class RobotMemberController {

	@Autowired
	private RobotMemberService robotMemberService;

	// id不填
	@RequestMapping("/addrobot")
	public BaseResult addrobot(String headImgUrl, String nickname) {
		if (StringUtils.isEmpty(headImgUrl) || StringUtils.isEmpty(nickname)) {
			throw new AnBangException("Robot参数不得为空");
		}
		RobotMemberDbo robot = new RobotMemberDbo();
		robot.setId(UUID.randomUUID().toString());
		String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
		robot.setUnionid(uuid);
		robot.setOpenid(uuid);
		robot.setGender("1");
		robot.setHeadimgurl(headImgUrl);
		robot.setNickname(nickname);
		robotMemberService.add(robot);

		synchronized (Robots.class) {
			Map<String, RobotMemberDbo> availableRobot = Robots.getAvailableRobot();
			availableRobot.put(uuid, robot);
			Robots.setAvailableRobot(availableRobot);
		}

		return new BaseResult();
	}

	@RequestMapping("/addgold")
	@ApiOperation("所有机器人加随机金币")
	public void addgold() {
		List<RobotMemberDbo> robotList = robotMemberService.findAllRobots();
		Map<String, String> loginMap = new HashMap<>();
		loginMap.put("unionid", "");
		loginMap.put("openid", "");
		loginMap.put("nickname", "");
		loginMap.put("headimgurl", "");
		loginMap.put("sex", "");

		Map<String, String> tryMap = new HashMap<>();
		tryMap.put("token", "");

		Map<String, String> goldMap = new HashMap<>();
		goldMap.put("memberId", "");
		goldMap.put("amount", "1000");
		goldMap.put("textSummary", "giveGoldToRobots");

		Random random = new Random();

		for (RobotMemberDbo memberDbo : robotList) {
			try {
				loginMap.replace("unionid", memberDbo.getUnionid());
				loginMap.replace("openid", memberDbo.getOpenid());
				loginMap.replace("nickname", memberDbo.getNickname());
				loginMap.replace("headimgurl", memberDbo.getHeadimgurl());
				loginMap.replace("sex", memberDbo.getGender());
				String loginPost = HttpUtils.doPost(UrlConfig.getWechatLoginUrl(), loginMap);

				String token = JSON.parseObject(loginPost).getJSONObject("data").getString("token");

				tryMap.replace("token", token);
				String memberPost = HttpUtils.doPost(UrlConfig.getTryTokenUrl(), tryMap);
				String memberId = JSON.parseObject(memberPost).getJSONObject("data").getString("memberId");

				int nextInt = random.nextInt(100);
				String amount = String.valueOf(nextInt + 300);
				goldMap.replace("amount", amount);
				goldMap.replace("memberId", memberId);
				String doPost = HttpUtils.doPost(UrlConfig.getGiveGoldUrl(), goldMap);
			} catch (Exception e) {
				continue;
			}
		}

	}

	@RequestMapping("/addsamegold")
	@ApiOperation("所有机器人加两万金币")
	public void addSameGold() {
		List<RobotMemberDbo> robotList = robotMemberService.findAllRobots();
		Map<String, String> loginMap = new HashMap<>();
		loginMap.put("unionid", "");
		loginMap.put("openid", "");
		loginMap.put("nickname", "");
		loginMap.put("headimgurl", "");
		loginMap.put("sex", "");

		Map<String, String> tryMap = new HashMap<>();
		tryMap.put("token", "");

		Map<String, String> goldMap = new HashMap<>();
		goldMap.put("memberId", "");
		goldMap.put("amount", "20000");
		goldMap.put("textSummary", "giveGoldToRobots");

		Random random = new Random();

		for (RobotMemberDbo memberDbo : robotList) {
			try {
				loginMap.replace("unionid", memberDbo.getUnionid());
				loginMap.replace("openid", memberDbo.getOpenid());
				loginMap.replace("nickname", memberDbo.getNickname());
				loginMap.replace("headimgurl", memberDbo.getHeadimgurl());
				loginMap.replace("sex", memberDbo.getGender());
				String loginPost = HttpUtils.doPost(UrlConfig.getWechatLoginUrl(), loginMap);

				String token = JSON.parseObject(loginPost).getJSONObject("data").getString("token");

				tryMap.replace("token", token);
				String memberPost = HttpUtils.doPost(UrlConfig.getTryTokenUrl(), tryMap);
				String memberId = JSON.parseObject(memberPost).getJSONObject("data").getString("memberId");
				goldMap.replace("memberId", memberId);
				String doPost = HttpUtils.doPost(UrlConfig.getGiveGoldUrl(), goldMap);

			} catch (Exception e) {
				continue;
			}
		}

	}

}
