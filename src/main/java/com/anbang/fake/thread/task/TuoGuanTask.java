//package com.anbang.fake.thread.task;
//
//import com.anbang.fake.thread.ConditionPool;
//import com.anbang.fake.websocket.MajiangRobotClient;
//import com.anbang.fake.websocket.MajiangTuoGuanClient;
//import com.anbang.fake.websocket.RobotClient;
//import com.anbang.fake.websocket.TuoGuanClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.URISyntaxException;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * @Author: 吴硕涵
// * @Date: 2019/1/25 4:29 PM
// * @Version 1.0
// */
//
//public class TuoGuanTask implements Runnable {
//    private Logger logger = LoggerFactory.getLogger(getClass());
//
//    String gametoken;
//    String wsUrl;
//    String gameId;
//    String memberId;
//
//    String gameType;
//
//
//    //传token过来也OK
//    public TuoGuanTask(String gameToken, String wsUrl, String gameId, String memberId, String gameType) {
//        this.gametoken = gameToken;
//        this.wsUrl = wsUrl;
//        this.gameId = gameId;
//        this.memberId = memberId;
//
//        this.gameType = gameType;
//    }
//
//
//    @Override
//    public void run() {
//        //TODO:REMOVE
//        ReentrantLock lock = new ReentrantLock();
//        String conditionId = UUID.randomUUID().toString();
//
//        lock.lock();//请求锁
//        Condition condition = lock.newCondition();
//
//        connectService(wsUrl, gametoken, gameId, conditionId);
////
//        try {
//            logger.info(Thread.currentThread() + "进入等待");
//            ConditionPool.addCondition(conditionId, condition);
//            ConditionPool.addLock(conditionId, lock);
//            condition.await();//设置当前线程进入等待
//
//        } catch (InterruptedException e) {
//
//        } finally {
//            lock.unlock();//释放锁
//        }
//
//        logger.info(Thread.currentThread() + "等待结束");
//        //没有任何异常回收
//        logger.info(memberId + "线程结束");
//    }
//
//
//    private void connectService(String wsUrl, String gameToken, String gameId, String conditionId) {
//        try {
//            if (gameType.equals("wenzhouShuangkou")) {
//                TuoGuanClient client = new TuoGuanClient(wsUrl, gameToken, gameId, memberId, conditionId);
//                client.connect();
//            } else {
//                MajiangTuoGuanClient client = new MajiangTuoGuanClient(wsUrl, gameToken, gameId, memberId, gameType, conditionId);
//                client.connect();
//            }
//            logger.info(memberId + "连接成功");
//        } catch (URISyntaxException e) {
//            logger.error("绑定玩家时遇到异常");
//        } catch (Exception e) {
//
//        }
//    }
//}
