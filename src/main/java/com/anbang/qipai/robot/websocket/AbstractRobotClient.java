package com.anbang.qipai.robot.websocket;

import com.alibaba.fastjson.JSON;
import com.anbang.qipai.robot.dao.dataObject.RobotMemberDbo;
import com.anbang.qipai.robot.model.CommonModel;
import com.anbang.qipai.robot.model.Robots;
import com.anbang.qipai.robot.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRobotClient  extends WebSocketClient {
    String robotId;
    String wsUrl;
    String name;
    String gameToken;
    String gameId;
    String memberId;

    long readyTime;
    long lastChuPaiTime;
    boolean hasReady;

    Map<String, String> configMap;
    Map<String, String> gameMap;
    boolean terminate = false;

    Logger logger = LoggerFactory.getLogger(MajiangRobotClient.class);
    ScheduledExecutorService scheduledExecutorService;

    public AbstractRobotClient(String url, String robotName, String gameToken, String gameId, String robotId, String memberId)
            throws URISyntaxException {
        super(new URI(url));
        this.wsUrl = url;
        this.name = robotName;
        this.gameToken = gameToken;
        this.gameId = gameId;
        this.memberId = memberId;

        gameMap = new HashMap<>();
        gameMap.put("token", gameToken);
        gameMap.put("gameId", gameId);
        this.robotId = robotId;

        this.hasReady = false;
        this.lastChuPaiTime = System.currentTimeMillis();
    }

    // 线程级别的定时任务
    void timer() {
        // Calendar c = Calendar.getInstance();
        // c.set(Calendar.HOUR_OF_DAY, 10); // 控制时
        // c.set(Calendar.MINUTE, 0); // 控制分
        // c.set(Calendar.SECOND, 0); // 控制秒
        //
        // Date time = c.getTime(); // 得到执行任务的时间,此处为当天的10：00：00

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!terminate) {
                    sendHeartBeat();
                } else {
                    logger.info("机器人" + name + "心跳结束");
                    System.gc();
                    Thread.currentThread().stop();
                }
            }

        }, 100, 7000, TimeUnit.MILLISECONDS);// 这里设定将延时每隔1000毫秒执行一次

    }

    void sendHeartBeat() {
        // 发送心跳的时候如果长时间不准备 机器人自己离开房间
        if (hasReady) {
            if (System.currentTimeMillis() - readyTime > 15 * 1000) {
                huishou();
                leaveRoom();
            }
        }

        if (System.currentTimeMillis() - lastChuPaiTime > 300 * 1000) {
            huishou();
            leaveRoom();
        }

        if (!StringUtils.isEmpty(wsUrl)) {
            CommonModel model = new CommonModel();
            model.setMsg("heartbeat");
            Map<String, String> map = new HashMap<>();
            map.put("token", gameToken);
            model.setData(map);
            send(JSON.toJSONString(model));
             logger.info("机器人" + name + "发送心跳");
        }
    }

    void leaveRoom() {
        Map<String, String> map = new HashMap<>();
        map.put("token", gameToken);
        try {
            HttpUtils.doPost(configMap.get("leavegame"), map);
        } catch (Exception e) {
        }
    }

    void huishou() {
        terminate = true;
        // 准备撤退
        synchronized (Robots.class) {
            Map<String, RobotMemberDbo> taskedRobot = Robots.getTaskedRobot();
            Map<String, RobotMemberDbo> availableRobot = Robots.getAvailableRobot();
            RobotMemberDbo memberDbo = taskedRobot.get(robotId);
            if (memberDbo != null) {
                availableRobot.put(robotId, memberDbo);
                logger.info(memberDbo.getNickname() + "加入可用队列");
                taskedRobot.remove(robotId);
            }
        }
        close();
        scheduledExecutorService.shutdownNow();
    }

    void bindPlayer() {
        CommonModel model = new CommonModel();
        model.setMsg("bindPlayer");
        model.setData(gameMap);
        send(JSON.toJSONString(model));
        logger.info(name + "绑定玩家成功");
    }
}
