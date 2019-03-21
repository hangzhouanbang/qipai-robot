package com.anbang.qipai.robot.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:27 PM
 * @Version 1.0
 */
public interface WenzhouGameRoomSink {

	String WENZHOUGAMEROOM = "wenzhouGameRoom";

	@Input
	SubscribableChannel wenzhouGameRoom();

}
