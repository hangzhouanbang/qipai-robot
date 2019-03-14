package com.anbang.qipai.robot.dao.mongodb;

import com.anbang.qipai.robot.dao.dataObject.RobotMemberDbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public void save(RobotMemberDbo memberDbo) {
        mongoTemplate.save(memberDbo);
    }

    @Override
    public void update(String memberId, String nickname, String headimgurl, String gender) {
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(memberId)),
                new Update().set("nickname", nickname).set("headimgurl", headimgurl).set("gender", gender),
                RobotMemberDbo.class);
    }

    @Override
    public void insert(RobotMemberDbo memberDbo) {
        mongoTemplate.insert(memberDbo);
    }

    @Override
    public List<RobotMemberDbo> findAll() {
        return mongoTemplate.findAll(RobotMemberDbo.class);
    }

}
