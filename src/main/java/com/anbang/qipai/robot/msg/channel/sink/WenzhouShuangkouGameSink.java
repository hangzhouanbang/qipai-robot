package com.anbang.qipai.robot.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface WenzhouShuangkouGameSink {
	String WENZHOUSHUANGKOUGAME = "wenzhouShuangkouGame";

	@Input
	SubscribableChannel wenzhouShuangkouGame();
}
