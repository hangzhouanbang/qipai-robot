package com.anbang.qipai.robot.cqrs.q.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.robot.cqrs.q.dao.AuthorizationDboDao;
import com.anbang.qipai.robot.cqrs.q.dao.RobotDboDao;
import com.anbang.qipai.robot.cqrs.q.dbo.AuthorizationDbo;
import com.anbang.qipai.robot.cqrs.q.dbo.RobotDbo;

@Component
public class MemberAuthQueryService {

	@Autowired
	private AuthorizationDboDao authorizationDboDao;

	@Autowired
	private RobotDboDao robotMemberDboDao;

	public AuthorizationDbo findThirdAuthorizationDbo(String publisher, String uuid) {
		return authorizationDboDao.find(true, publisher, uuid);
	}

	public AuthorizationDbo findAuthorizationDboByMemberIdAndPublisher(String memberId, String publisher) {
		return authorizationDboDao.findAuthorizationDboByMemberIdAndPublisher(true, memberId, publisher);
	}

	public void addThirdAuth(AuthorizationDbo authDbo) {
		authorizationDboDao.save(authDbo);
	}

	public void add(RobotDbo robotMemberDbo) {
		robotMemberDboDao.insert(robotMemberDbo);
	}

	public void updateMemberPhone(String memberId, String phone) {
		robotMemberDboDao.updateMemberPhone(memberId, phone);
	}

	public void updateMemberBaseInfo(String memberId, String nickname, String headimgurl, String gender) {
		robotMemberDboDao.updateMemberBaseInfo(memberId, nickname, headimgurl, gender);
	}

	public void updateMemberRealUser(String memberId, String IDcard) {
		robotMemberDboDao.updateMemberRealUser(memberId, IDcard);
	}

	public List<RobotDbo> findAllRobots(int page, int size) {
		return robotMemberDboDao.findAll(page, size);
	}

	public long countRobotAmount() {
		return robotMemberDboDao.countAmount();
	}
}
