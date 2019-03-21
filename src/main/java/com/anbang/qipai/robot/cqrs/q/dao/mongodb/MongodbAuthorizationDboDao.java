package com.anbang.qipai.robot.cqrs.q.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.anbang.qipai.robot.cqrs.q.dao.AuthorizationDboDao;
import com.anbang.qipai.robot.cqrs.q.dbo.AuthorizationDbo;

@Component
public class MongodbAuthorizationDboDao implements AuthorizationDboDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public AuthorizationDbo find(boolean thirdAuth, String publisher, String uuid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("thirdAuth").is(thirdAuth));
		query.addCriteria(Criteria.where("publisher").is(publisher));
		query.addCriteria(Criteria.where("uuid").is(uuid));
		return mongoTemplate.findOne(query, AuthorizationDbo.class);
	}

	@Override
	public void save(AuthorizationDbo authDbo) {
		mongoTemplate.insert(authDbo);
	}

	@Override
	public AuthorizationDbo findAuthorizationDboByMemberIdAndPublisher(boolean thirdAuth, String memberId,
			String publisher) {
		Query query = new Query();
		query.addCriteria(Criteria.where("thirdAuth").is(thirdAuth));
		query.addCriteria(Criteria.where("publisher").is(publisher));
		query.addCriteria(Criteria.where("memberId").is(memberId));
		return mongoTemplate.findOne(query, AuthorizationDbo.class);
	}

}
