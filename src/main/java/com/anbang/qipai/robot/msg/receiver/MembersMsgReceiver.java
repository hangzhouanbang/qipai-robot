package com.anbang.qipai.robot.msg.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.robot.cqrs.q.dbo.RobotDbo;
import com.anbang.qipai.robot.cqrs.q.service.MemberAuthQueryService;
import com.anbang.qipai.robot.msg.channel.sink.MembersSink;
import com.anbang.qipai.robot.msg.msjobj.CommonMO;
import com.google.gson.Gson;

@EnableBinding(MembersSink.class)
public class MembersMsgReceiver {

	@Autowired
	private MemberAuthQueryService memberAuthQueryService;

	private Gson gson = new Gson();

	@StreamListener(MembersSink.MEMBERS)
	public void recordMember(CommonMO mo) {
		String msg = mo.getMsg();
		String json = gson.toJson(mo.getData());
		RobotDbo member = gson.fromJson(json, RobotDbo.class);
		if (member.isRobot()) {
			if ("newMember".equals(msg)) {
				memberAuthQueryService.add(member);
			}
			if ("update member phone".equals(msg)) {
				memberAuthQueryService.updateMemberPhone(member.getId(), member.getPhone());
			}
			if ("update member info".equals(msg)) {
				memberAuthQueryService.updateMemberBaseInfo(member.getId(), member.getNickname(),
						member.getHeadimgurl(), member.getGender());
			}
			if ("update member realUser".equals(msg)) {
				memberAuthQueryService.updateMemberRealUser(member.getId(), member.getIdCard());
			}
		}
	}

}
