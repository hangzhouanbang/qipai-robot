package com.anbang.qipai.robot.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface DoudizhuGameRoomSink {
    //注意：配置文件中的路径与@StreamListener注解中的value一致
    String CHANNEL = "doudizhuGameRoom";


    //是一个bean
    @Input
    SubscribableChannel doudizhuGameRoom();
}
