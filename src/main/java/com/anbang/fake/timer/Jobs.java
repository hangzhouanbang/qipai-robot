//package com.anbang.fake.timer;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.anbang.fake.config.UrlConfig;
//import com.anbang.fake.dao.dataObject.RobotMemberDbo;
//import com.anbang.fake.exceptions.AnBangException;
//import com.anbang.fake.model.Robots;
//import com.anbang.fake.service.RobotGameService;
//import com.anbang.fake.thread.ThreadPool;
//import com.anbang.fake.thread.ThreadPoolFactory;
//import com.anbang.fake.thread.task.JoinGameTask;
//import com.anbang.fake.utils.HttpUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//
///**
// * @Author: 吴硕涵
// * @Date: 2019/1/14 2:17 PM
// * @Description 仅供测试使用的定时任务
// * @Version 1.0
// */
//@Component
//public class Jobs {
//    private final static Logger logger = LoggerFactory.getLogger(Jobs.class);
//
//    @Autowired
//    private RobotGameService robotGameService;
//
//    public final static long Eight_Second = 3 * 1000;
//
//
//    @Scheduled(fixedDelay = Eight_Second)
//    public void fixedDelayJob() {
//
//        try {
//            String doPost = HttpUtils.doPost(UrlConfig.getFindRoomUrl(), new HashMap<>());
//            logger.info("查找房间" + doPost);
//            JSONObject jsonObject = JSON.parseObject(doPost);
//            JSONArray roomList = jsonObject.getJSONArray("data");
//            for (int index = 0; index < roomList.size(); index++) {
//                JSONObject roomObject = roomList.getJSONObject(index);
//                String roomNumber = roomObject.getString("no");
//                if (StringUtils.isEmpty(roomNumber)) {
//                    continue;
//                }
//                String gameType = roomObject.getString("game");
////                if (!gameType.equals("wenzhouShuangkou")) {
////                    continue;
////                }
//
////                if (!gameType.equals("wenzhouMajiang")) {
////                    continue;
////                }
//
//                if (!gameType.equals("ruianMajiang")&&!gameType.equals("wenzhouShuangkou")) {
//                    continue;
//                }
//
//
//                synchronized (this) {
//                    Map<String, RobotMemberDbo> availableRobot = Robots.getAvailableRobot();
//                    Map<String, RobotMemberDbo> taskedRobot = Robots.getTaskedRobot();
//
//                    //随机选一个机器人
//                    if (availableRobot.size() == 0) {
//                        throw new AnBangException("机器人不够啦");
//                    }
//
//                    Random random = new Random();
//                    int rand = random.nextInt(availableRobot.size());
//                    String[] keyArray = availableRobot.keySet().toArray(new String[0]);
//                    String key = keyArray[rand];
//                    RobotMemberDbo robot = availableRobot.get(key);
//
//                    //增加任务队列 减少可用队列
//                    availableRobot.remove(key);
//                    taskedRobot.put(key, robot);
//                    Robots.setAvailableRobot(availableRobot);
//                    Robots.setTaskedRobot(taskedRobot);
//                    logger.info("机器人" + robot.getNickname() + "移除可用队列");
//
//
//                    JoinGameTask task = new JoinGameTask(roomNumber,
//                            gameType, robot.getNickname(),
//                            robot.getGender(), robot.getHeadimgurl(),
//                            robot.getUnionid(), robot.getOpenid(), robot.getId());
//
//                    ThreadPool threadPool = ThreadPoolFactory.getThreadPool(gameType);
//
//                    threadPool.execute(task);
//                }
//            }
//
//        } catch (Exception e) {
////            logger.error("JSON转换错误");
//        }
//
//    }
//
//}
