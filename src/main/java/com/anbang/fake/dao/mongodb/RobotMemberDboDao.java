package com.anbang.fake.dao.mongodb;

import com.anbang.fake.dao.dataObject.RobotMemberDbo;

import java.util.List;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/14 11:13 AM
 * @Version 1.0
 */
public interface RobotMemberDboDao {
    void save(RobotMemberDbo memberDbo);

    void update(String memberId, String nickname, String headimgurl, String gender);

    void insert(RobotMemberDbo memberDbo);

    List<RobotMemberDbo> findAll();

}
