package com.anbang.qipai.robot.cqrs.q.dao;

import java.util.List;

import com.anbang.qipai.robot.cqrs.q.dbo.RobotDbo;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:18 PM
 * @Version 1.0
 */
public interface RobotMemberDboDao {

	void updateMemberPhone(String memberId, String phone);

	void updateMemberBaseInfo(String memberId, String nickname, String headimgurl, String gender);

	void updateMemberRealUser(String memberId, String IDcard);

	void insert(RobotDbo memberDbo);

	List<RobotDbo> findAll(int page, int size);

	long countAmount();
}
