package com.anbang.qipai.robot.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MembersSink {

	String MEMBERS = "members";

	@Input
	SubscribableChannel members();

}
