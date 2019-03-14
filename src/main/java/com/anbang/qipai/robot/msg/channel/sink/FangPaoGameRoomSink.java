package com.anbang.qipai.robot.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:27 PM
 * @Version 1.0
 */
public interface FangPaoGameRoomSink {
    //注意：配置文件中的路径与@StreamListener注解中的value一致
    String CHANNEL = "fangpaoGameRoom";


    //是一个bean
    @Input
    SubscribableChannel fangpaoGameRoom();

}
