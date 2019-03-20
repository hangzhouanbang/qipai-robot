package com.anbang.qipai.robot.plan.dao;

import java.util.List;

import com.anbang.qipai.robot.plan.bean.RobotMemberDbo;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:18 PM
 * @Version 1.0
 */
public interface RobotMemberDboDao {
    void save(RobotMemberDbo memberDbo);

    void update(String memberId, String nickname, String headimgurl, String gender);

    void insert(RobotMemberDbo memberDbo);

    List<RobotMemberDbo> findAll();
}
