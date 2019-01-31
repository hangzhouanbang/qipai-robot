package com.anbang.fake.timer;

import com.anbang.fake.dao.dataObject.RobotMemberDbo;
import com.anbang.fake.dao.mongodb.RobotMemberDboDao;
import com.anbang.fake.model.Robots;
import com.anbang.fake.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/14 10:08 AM
 * @Version 1.0
 */
@Component()
public class InitTimer implements
        ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RobotMemberDboDao robotMemberDboDao;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println(LoggerUtils.getSplitLogger("spring初始化完毕"));
        System.out.println(LoggerUtils.getSplitLogger("进行项目初始化"));

        List<RobotMemberDbo> allRobots = robotMemberDboDao.findAll();
        Map<String,RobotMemberDbo> map = new HashMap<>();
        for (RobotMemberDbo memberDbo : allRobots) {
            map.put(memberDbo.getId(),memberDbo);
        }
        Robots.setAvailableRobot(map);
        System.out.println(LoggerUtils.getSplitLogger("机器人初始化成功"));
    }
}
