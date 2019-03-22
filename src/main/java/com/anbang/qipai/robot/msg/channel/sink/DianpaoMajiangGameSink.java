package com.anbang.qipai.robot.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface DianpaoMajiangGameSink {
	String DIANPAOMAJIANGGAME = "dianpaoMajiangGame";

	@Input
	SubscribableChannel dianpaoMajiangGame();
}
