package com.anbang.qipai.robot.cqrs.q.dao.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.anbang.qipai.robot.cqrs.q.dao.RobotMemberDboDao;
import com.anbang.qipai.robot.cqrs.q.dbo.RobotDbo;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:19 PM
 * @Version 1.0
 */

@Component
public class MongodbRobotMemberDboDao implements RobotMemberDboDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void insert(RobotDbo memberDbo) {
		mongoTemplate.insert(memberDbo);
	}

	@Override
	public void updateMemberPhone(String memberId, String phone) {
		Query query = new Query(Criteria.where("id").is(memberId));
		Update update = new Update();
		update.set("phone", phone);
		mongoTemplate.updateFirst(query, update, RobotDbo.class);
	}

	@Override
	public void updateMemberBaseInfo(String memberId, String nickname, String headimgurl, String gender) {
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(memberId)),
				new Update().set("nickname", nickname).set("headimgurl", headimgurl).set("gender", gender),
				RobotDbo.class);
	}

	@Override
	public void updateMemberRealUser(String memberId, String IDcard) {
		Query query = new Query(Criteria.where("id").is(memberId));
		Update update = new Update();
		update.set("idCard", IDcard);
		mongoTemplate.updateFirst(query, update, RobotDbo.class);
	}

	@Override
	public List<RobotDbo> findAll(int page, int size) {
		Query query = new Query();
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, RobotDbo.class);
	}

	@Override
	public long countAmount() {
		Query query = new Query();
		return mongoTemplate.count(query, RobotDbo.class);
	}

}
