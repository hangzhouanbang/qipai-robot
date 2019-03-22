package com.anbang.qipai.robot.plan.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.anbang.qipai.robot.plan.bean.PlayerInfo;
import com.anbang.qipai.robot.plan.dao.PlayerInfoDao;
import com.anbang.qipai.robot.plan.dao.mongodb.repository.PlayerInfoRepository;

@Component
public class MongodbPlayerInfoDao implements PlayerInfoDao {

	@Autowired
	private PlayerInfoRepository repository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PlayerInfo findById(String id) {
		return repository.findOne(id);
	}

	@Override
	public void save(PlayerInfo playerInfo) {
		repository.save(playerInfo);
	}

	@Override
	public void updateVip(String playerId, boolean vip) {
		Query query = new Query(Criteria.where("id").is(playerId));
		Update update = new Update();
		update.set("vip", vip);
		mongoTemplate.updateFirst(query, update, PlayerInfo.class);
	}

	@Override
	public void updateMemberBaseInfo(String memberId, String nickname, String headimgurl, String gender) {
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(memberId)),
				new Update().set("nickname", nickname).set("headimgurl", headimgurl).set("gender", gender),
				PlayerInfo.class);
	}

}
