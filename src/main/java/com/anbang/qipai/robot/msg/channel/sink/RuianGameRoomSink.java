package com.anbang.qipai.robot.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 11:40 AM
 * @Version 1.0
 */
public interface RuianGameRoomSink {

	String RUIANGAMEROOM = "ruianGameRoom";

	@Input
	SubscribableChannel ruianGameRoom();
}
