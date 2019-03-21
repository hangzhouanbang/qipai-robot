package com.anbang.qipai.robot.msg.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.robot.cqrs.q.dbo.AuthorizationDbo;
import com.anbang.qipai.robot.cqrs.q.service.MemberAuthQueryService;
import com.anbang.qipai.robot.msg.channel.sink.AuthorizationSink;
import com.anbang.qipai.robot.msg.msjobj.CommonMO;
import com.google.gson.Gson;

@EnableBinding(AuthorizationSink.class)
public class AuthorizationMsgReceiver {

	@Autowired
	private MemberAuthQueryService memberAuthQueryService;

	private Gson gson = new Gson();

	@StreamListener(AuthorizationSink.AUTHORIZATION)
	public void authorization(CommonMO mo) {
		String msg = mo.getMsg();
		String json = gson.toJson(mo.getData());
		if ("new authorization".equals(msg)) {
			AuthorizationDbo authDbo = gson.fromJson(json, AuthorizationDbo.class);
			if (authDbo.getPublisher().equals("union.robot") || authDbo.getPublisher().equals("open.robot.app.qipai")) {
				memberAuthQueryService.addThirdAuth(authDbo);
			}
		}
	}
}
