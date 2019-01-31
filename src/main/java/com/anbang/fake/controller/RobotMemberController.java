package com.anbang.fake.controller;

import com.alibaba.fastjson.JSON;
import com.anbang.fake.config.UrlConfig;
import com.anbang.fake.dao.dataObject.RobotMemberDbo;
import com.anbang.fake.exceptions.AnBangException;
import com.anbang.fake.model.BaseResult;
import com.anbang.fake.model.Robots;
import com.anbang.fake.service.RobotMemberService;
import com.anbang.fake.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.*;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/14 11:22 AM
 * @Version 1.0
 */

//@CrossOrigin
@RestController
@RequestMapping("/member")
public class RobotMemberController {

    Logger logger = LoggerFactory.getLogger(RobotMemberController.class);

    @Autowired
    private RobotMemberService robotMemberService;


    //id不填
    @RequestMapping("/addrobot")
    public BaseResult addrobot(String headImgUrl, String nickname) {
        if (StringUtils.isEmpty(headImgUrl) ||
                StringUtils.isEmpty(nickname)
                ) {
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
    public void addgold() {
        List<RobotMemberDbo> robotList = robotMemberService.findAllRobots();
        logger.info("数据库里一共有" + robotList.size() + "个机器人");
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
                logger.info("赠送给账户" + goldMap);
                String doPost = HttpUtils.doPost(UrlConfig.getGiveGoldUrl(), goldMap);
                logger.info("赠送金币" + doPost);

            } catch (Exception e) {
                continue;
            }
        }

    }


}
