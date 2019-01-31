package com.anbang.fake.service;

import com.anbang.fake.dao.dataObject.RobotMemberDbo;
import com.anbang.fake.dao.mongodb.RobotMemberDboDao;
import com.anbang.fake.thread.ThreadPoolFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/14 11:40 AM
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
