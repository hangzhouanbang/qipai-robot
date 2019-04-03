package com.anbang.qipai.robot.thread.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anbang.qipai.robot.config.UrlConfig;
import com.anbang.qipai.robot.dao.dataObject.RobotMemberDbo;
import com.anbang.qipai.robot.exceptions.AnBangException;
import com.anbang.qipai.robot.model.Robots;
import com.anbang.qipai.robot.thread.ConditionPool;
import com.anbang.qipai.robot.utils.HttpUtils;
import com.anbang.qipai.robot.websocket.DoudizhuRobotClient;
import com.anbang.qipai.robot.websocket.MajiangRobotClient;
import com.anbang.qipai.robot.websocket.RobotClient;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 1:07 PM
 * @Version 1.0
 */
public class JoinGameTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(getClass());

//    private ReentrantLock lock = new ReentrantLock();

//    public Condition condition = lock.newCondition();

//    public volatile boolean exit = false;

    //暂时不用token
//    private String token;

    private String wsUrl;
    private String gameToken;
    private String gameId;
    private String gameType;
    private String robotId;  //数据库里的robotID  即为UUID  一人一个
    private String nickname;// 会员昵称
    private String gender;// 会员性别:男:male,女:female
    private String headimgurl;// 头像url
    private String unionid;
    private String openid;

    private String memberId;

//    private String webSocketUrl;

    public JoinGameTask(String gameId, String gameType, String nickname, String gender, String headimgurl, String unionid, String openid, String robotId) {
        this.gameId = gameId;
        this.gameType = gameType;
        this.nickname = nickname;
        this.gender = gender;
        this.headimgurl = headimgurl;
        this.unionid = unionid;
        this.openid = openid;
        this.robotId = robotId;
    }

    JoinGameTask() {
    }


    @Override
    public void run() {
        //TODO:REMOVE

//        ReentrantLock lock = new ReentrantLock();
//        conditionId = UUID.randomUUID().toString();
//        lock.lock();//请求锁
//        Condition condition = lock.newCondition();//lock的condition


        String token = postAndGetToken();
        getInfo(token);//获取memberID 和 wsURL
        joinRoom(token);//获取游戏的gametoken  和 wsURL

        logger.info(nickname + "连接客户端入参" + wsUrl + " " + gameToken + " " + gameId);
        connectService(wsUrl, gameToken, gameId);
//
//        try {
//            logger.info(Thread.currentThread() + "进入等待");
//            ConditionPool.addCondition(conditionId, condition);
//            ConditionPool.addLock(conditionId, lock);
//            condition.await();//设置当前线程进入等待
//
//        } catch (InterruptedException e) {
//
//        } finally {
//            logger.info(Thread.currentThread() + "释放锁");
//            lock.unlock();//释放锁
//        }

        logger.info(Thread.currentThread() + "等待结束");
        //没有任何异常回收
//        huishou();
        logger.info(nickname + "分配线程结束");

    }


    private String postAndGetToken() {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("unionid", unionid);
            map.put("openid", openid);
            map.put("nickname", nickname);
            map.put("headimgurl", headimgurl);
            Random random = new Random();
            if(random.nextInt(2)==0) {
                gender = "1";
            }else {
                gender = "2";
            }

            map.put("sex", gender);
            logger.info(nickname + "请求token");
            String postResult = HttpUtils.doPost(UrlConfig.getWechatLoginUrl(), map);
            logger.info(nickname + "请求token的结果" + postResult);
            JSONObject object = JSON.parseObject(postResult);
            String token = object.getJSONObject("data").getString("token");
            return token;
        } catch (IOException e) {
            huishou();
            throw new AnBangException("获取token失败");
        }
    }

    private void joinRoom(String token) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            map.put("gameId", gameId);
            map.put("game", gameType);
            logger.info(nickname + "请求加入房间" + map.toString());


            String doPost = HttpUtils.doPost(UrlConfig.getJoinXiuXianChang(), map);
            logger.info(nickname + "加入房间的结果" + doPost);

            JSONObject object = JSON.parseObject(doPost);

            gameToken = object.getJSONObject("data").getString("token");
            wsUrl = object.getJSONObject("data").getString("wsUrl");

        } catch (Exception e) {
            e.printStackTrace();
            huishou();
            throw new AnBangException(Thread.currentThread() + nickname + "加入房间失败");
        }
    }


    private void connectService(String wsUrl, String gameToken, String gameId) {
        try {

            if (gameType.equals("wenzhouShuangkou")) {
                RobotClient client = new RobotClient(wsUrl, nickname, gameToken, gameId, robotId, memberId);
                client.connect();
            } else if(gameType.equals("doudizhu")){
                DoudizhuRobotClient client = new DoudizhuRobotClient(wsUrl, nickname, gameToken, gameId, robotId, memberId);
                client.connect();
            } else {
                MajiangRobotClient client = new MajiangRobotClient(wsUrl, nickname, gameToken, gameId, robotId, memberId, gameType);
                client.connect();
            }
            logger.info(nickname + "连接成功");
        } catch (URISyntaxException e) {
            huishou();
            throw new AnBangException("绑定玩家时遇到异常");
        } catch (Exception e) {

        }
    }


    private void huishou() {
        synchronized (Robots.class) {
            Map<String, RobotMemberDbo> taskedRobot = Robots.getTaskedRobot();
            Map<String, RobotMemberDbo> availableRobot = Robots.getAvailableRobot();
            RobotMemberDbo memberDbo = taskedRobot.get(robotId);
            if (memberDbo != null) {
                availableRobot.put(robotId, memberDbo);
                taskedRobot.remove(robotId);
                logger.info(nickname + "加入可用队列");
            }
        }
    }

    private void getInfo(String token) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        try {
            String doPost = HttpUtils.doPost(UrlConfig.getHallIndexUrl(), map);
            logger.info(nickname + "获取游戏的信息" + doPost);
            this.memberId = JSON.parseObject(doPost).getJSONObject("data").getJSONObject("member").getString("id");
//            this.wsUrl = JSON.parseObject(doPost).getJSONObject("data").getString("wsUrl");
        } catch (Exception e) {
            huishou();
            throw new AnBangException("获取机器人信息时异常");
        }
    }



}
