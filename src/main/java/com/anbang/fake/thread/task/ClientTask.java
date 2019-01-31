package com.anbang.fake.thread.task;

import com.anbang.fake.websocket.RobotClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/18 3:22 PM
 * @Version 1.0
 */
public class ClientTask implements Runnable {

    Logger logger = LoggerFactory.getLogger(ClientTask.class);

    private RobotClient robotClient;

    @Override
    public void run() {
        //连接
        //挂起
    }
}
