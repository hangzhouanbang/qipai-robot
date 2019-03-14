package com.anbang.qipai.robot.msg.channel.sink.tuoguan;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author: 吴硕涵
 * @Date: 2019/3/4 2:33 PM
 * @Version 1.0
 */
public interface RuianMajiangGame {
    //注意：配置文件中的路径与@StreamListener注解中的value一致
    String CHANNEL = "ruianMajiangGame";

    //是一个bean
    @Input
    SubscribableChannel ruianMajiangGame();
}
