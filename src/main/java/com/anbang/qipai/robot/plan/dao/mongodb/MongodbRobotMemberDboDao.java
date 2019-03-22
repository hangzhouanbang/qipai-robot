package com.anbang.qipai.robot.plan.dao.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.anbang.qipai.robot.plan.bean.RobotMemberDbo;
import com.anbang.qipai.robot.plan.dao.RobotMemberDboDao;

@Component
public class MongodbRobotMemberDboDao implements RobotMemberDboDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<RobotMemberDbo> findAll() {
		return mongoTemplate.findAll(RobotMemberDbo.class, "robotMemberDbo");
	}

}
