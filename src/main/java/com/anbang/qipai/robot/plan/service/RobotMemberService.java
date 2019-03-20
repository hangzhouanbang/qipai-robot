package com.anbang.qipai.robot.plan.service;

import com.anbang.qipai.robot.plan.bean.RobotMemberDbo;
import com.anbang.qipai.robot.plan.dao.RobotMemberDboDao;
import com.anbang.qipai.robot.thread.ThreadPoolFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 1:10 PM
 * @Version 1.0
 */

@Component
public class RobotMemberService {
    @Autowired
    private RobotMemberDboDao robotMemberDboDao;

    public void add(RobotMemberDbo robotMemberDbo) {
        robotMemberDboDao.insert(robotMemberDbo);
    }

    public void selectOneRobot(String gameTypeName) {
        ThreadPoolFactory.getThreadPool(gameTypeName);
    }

    public List<RobotMemberDbo> findAllRobots() {
        return robotMemberDboDao.findAll();
    }
}
