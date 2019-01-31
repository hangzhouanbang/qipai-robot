//package com.anbang.qipai.robot.websocket;
//
///**
// * @Author: 吴硕涵
// * @Date: 2019/1/31 1:12 PM
// * @Version 1.0
// */
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.anbang.qipai.robot.exceptions.AnBangException;
//import com.anbang.qipai.robot.utils.HttpUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.TimerTask;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;ublic class MajiangTuoGuanClient extends WebSocketClient {
//    Logger logger = LoggerFactory.getLogger(MajiangTuoGuanClient.class);
//
//
//    private String wsUrl;
//    private String gameToken;
//    private String gameId;
//    private String memberId;
//
//    private Map<String, String> configMap;
//
//    private boolean terminate = false;
//
//    private String fatherConditionId;
//
//    private Map<String, String> gameMap;
//
//    public MajiangTuoGuanClient(String url,String gameToken, String gameId, String memberId, String gameType, String conditionId) throws URISyntaxException {
//        super(new URI(url));
//        this.wsUrl = url;
//        this.gameToken = gameToken;
//        this.gameId = gameId;
//        this.memberId = memberId;
//
//        gameMap = new HashMap<>();
//        gameMap.put("token", gameToken);
//        gameMap.put("gameId", gameId);
//
//        this.fatherConditionId = conditionId;
//
//        if (gameType.equals("wenzhouMajiang")) {
//            configMap = MajiangUrlConfig.getWenzhouMajiang();
//        } else if (gameType.equals("ruianMajiang")) {
//            configMap = MajiangUrlConfig.getRuianMajiang();
//        }
//    }
//
//    private void notifyAndRefresh() {
////        唤醒
//        ConditionPool.reentrantLockMap.get(fatherConditionId).lock();
//        ConditionPool.conditionMap.get(fatherConditionId).signal();
//
//        ConditionPool.removeCondition(fatherConditionId);
//        ConditionPool.removeLock(fatherConditionId);
//    }
//
//    @Override
//    public void onMessage(String paramString) {
//        logger.info("托管用户" + memberId + "socket接收到消息：" + paramString);
//        JSONObject messageObject = JSON.parseObject(paramString);
//
////        try {
//
//        if (messageObject.getString("msg").equals("bindPlayer")) {
//            //不可能
//
//        } else if (messageObject.getString("msg").equals("query")) {
//            if (messageObject.getJSONObject("data").getString("scope").equals("maidiState")) {
//                maidi();
//            } else if (messageObject.getJSONObject("data").getString("scope").equals("panForMe")) {
//                panForMe();
//            } else if (messageObject.getJSONObject("data").getString("scope").equals("panResult")) {
//                readyToNextGame();  //准备下一盘
//            } else if (messageObject.getJSONObject("data").getString("scope").equals("juResult")) {
//                huishou();
//            } else if (messageObject.getJSONObject("data").getString("scope").equals("gameFinishVote")) {
//                gameFinishVote();
//
//                if (queryVote()) {
//                    huishou();
//                }
//            }
//
//        }
//    }
//
//
//    @Override
//    public void onOpen(ServerHandshake shake) {
//        logger.info("托管用户" + memberId + "建立三次握手");
//        timer();
//        logger.info("托管用户" + memberId + "定时任务开始");
//    }
//
//
//    @Override
//    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
//        logger.info("托管用户" + memberId + "关闭...");
//    }
//
//    @Override
//    public void onError(Exception e) {
//        e.printStackTrace();
//        throw new AnBangException(memberId + "Client遇到异常");
//    }
//
//    private void maidi() {
//        Map<String, String> map = new HashMap<>();
//        map.put("token", gameToken);
//        map.put("yes", "true");
//        try {
//
//            String maidi = HttpUtils.doPost(configMap.get("maidi"), map);
//
//            logger.info(memberId + "买底" + maidi);
//            if (JSON.parseObject(maidi).getString("success").equals("true")) {
//                if (JSON.parseObject(maidi).getJSONObject("data").getJSONArray("queryScopes").contains("panForMe")) {
//                    panForMe();
//                }
//            }
//            logger.info(memberId + "买底成功");
//        } catch (Exception e) {
//            huishou();
//            throw new AnBangException(memberId + "买底时异常");
//        }
//    }
//
//
//    private void panForMe() {
//        try {
//
//            String doPost = HttpUtils.doPost(configMap.get("panforme"), gameMap);
//            logger.info(memberId + "请求panForMe的结果" + doPost);
//            JSONObject jsonObject = JSONObject.parseObject(doPost);
//            if (jsonObject.getString("success").equals("false")) {
//                return;
//            }
//
//            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject("panActionFrame").getJSONObject("panAfterAction")
//                    .getJSONArray("playerList");
//
//            for (int index = 0; index < jsonArray.size(); index++) {
//                if (jsonArray.getJSONObject(index).getString("id").equals(memberId)) {
//                    action(jsonArray.getJSONObject(index));  //到自己了
//                    break;
//                }
//            }
//
//        } catch (Exception e) {
//            huishou();
//            throw new AnBangException(memberId + "请求panforme异常");
//        }
//    }
//
//    private void action(JSONObject jsonObject) {
//        logger.info(memberId + "可以做的动作" + jsonObject.toJSONString());
//        JSONArray actionCandidates = jsonObject.getJSONArray("actionCandidates");
//        if (actionCandidates == null) {
//            return;
//        }
//
//        //摸是单独的
//        if (actionCandidates.toJSONString().contains("mo")) {
//            String id = actionCandidates.getJSONObject(0).getString("id");
//            postAction(id);
//        } else {
//
//            //1、胡  2、杠  3、碰 4、吃 5、打 6、过
//            MajiangPlayerAction[] playerActions = new MajiangPlayerAction[6];
//            for (int index = 0; index < actionCandidates.size(); index++) {
//                String type = actionCandidates.getJSONObject(index).getString("type");
//                if (type.equals("hu")) {
//                    MajiangPlayerAction action = new MajiangPlayerAction();
//                    action.setId(actionCandidates.getJSONObject(index).getString("id"));
//                    action.setType("hu");
//                    playerActions[0] = action;
//                } else if (type.equals("gang")) {
//                    MajiangPlayerAction action = new MajiangPlayerAction();
//                    action.setId(actionCandidates.getJSONObject(index).getString("id"));
//                    action.setType("gang");
//                    playerActions[1] = action;
//                } else if (type.equals("peng")) {
//                    MajiangPlayerAction action = new MajiangPlayerAction();
//                    action.setId(actionCandidates.getJSONObject(index).getString("id"));
//                    action.setType("peng");
//                    playerActions[2] = action;
//                } else if (type.equals("chi")) {
//                    MajiangPlayerAction action = new MajiangPlayerAction();
//                    action.setId(actionCandidates.getJSONObject(index).getString("id"));
//                    action.setType("chi");
//                    playerActions[3] = action;
//                } else if (type.equals("da")) {
//                    if (playerActions[4] == null) {
//                        MajiangPlayerAction action = new MajiangPlayerAction();
//                        action.add(actionCandidates.getJSONObject(index).getString("id"),
//                                actionCandidates.getJSONObject(index).getString("pai")
//                        );
//                        playerActions[4] = action;
//                    } else {
//                        playerActions[4].add(actionCandidates.getJSONObject(index).getString("id"),
//                                actionCandidates.getJSONObject(index).getString("pai"));
//                    }
//                } else if (type.equals("guo")) {
//                    MajiangPlayerAction action = new MajiangPlayerAction();
//                    action.setId(actionCandidates.getJSONObject(index).getString("id"));
//                    action.setType("guo");
//                    playerActions[5] = action;
//                }
//            }
//
//
//            //找优先级高的先打
//            for (int index = 0; index < 6; index++) {
//                if (playerActions[index] != null) {
//                    try {
//                        synchronized (this) {
//                            wait(1500);
//                        }
//                    } catch (Exception e) {
//                    }
//                    logger.info(memberId + "执行动作" + playerActions[index].getType());
//                    postAction(playerActions[index].getId());
//                    break;
//                }
//            }
//        }
//
//    }
//
//    private void postAction(String actionId) {
//        Map<String, String> map = new HashMap<>();
//        map.put("token", gameToken);
//        map.put("id", actionId);
//        logger.info(memberId + "发送action请求" + map.toString());
//        try {
//
//            String action = HttpUtils.doPost(configMap.get("action"), map);
//            logger.info(memberId + "请求postAction结果" + action);
//            if (JSON.parseObject(action).getJSONObject("data").getJSONArray("queryScopes").contains("panForMe")) {
//                panForMe();
//            }
//            if (JSON.parseObject(action).getJSONObject("data").getJSONArray("queryScopes").contains("panResult")) {
//                readyToNextGame();
//            }
//        } catch (Exception e) {
//            huishou();
//            throw new AnBangException(memberId + "请求action异常");
//        }
//    }
//
//    //线程级别的定时任务
//    public void timer() {
//        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        scheduledExecutorService.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (!terminate) {
//                    sendHeartBeat();
//                } else {
//                    logger.info("托管用户" + memberId + "心跳结束");
//                    System.gc();
//                    Thread.currentThread().stop();
//                }
//            }
//
//        }, 100, 7000, TimeUnit.MILLISECONDS);// 这里设定将延时每隔1000毫秒执行一次
//
//    }
//
//    public void sendHeartBeat() {
//        if (!StringUtils.isEmpty(wsUrl)) {
//            CommonModel model = new CommonModel();
//            model.setMsg("heartbeat");
//            Map<String, String> map = new HashMap<>();
//            map.put("token", gameToken);
//            model.setData(map);
//            send(JSON.toJSONString(model));
//        }
//    }
//
//
//    private void readyToNextGame() {
//        Map<String, String> map = new HashMap<>();
//        map.put("token", gameToken);
//        try {
//            synchronized (this) {
//                wait(1000);
//            }
//            String doPost = HttpUtils.doPost(configMap.get("readyNext"), map);
//            logger.info(memberId + "准备第二盘" + doPost);
//        } catch (Exception e) {
//            huishou();
//            throw new AnBangException(memberId + "准备第二盘异常");
//        }
//    }
//
//    private void gameFinishVote() {
//        Map<String, String> map = new HashMap<>();
//        map.put("token", gameToken);
//        map.put("yes", "true");
//        try {
//            synchronized (this) {
//                wait(1000);
//            }
//
//            String doPost = HttpUtils.doPost(configMap.get("vote"), map);
//            logger.info(memberId + "投票请求" + doPost);
//        } catch (Exception e) {
//            huishou();
//            throw new AnBangException(memberId + "游戏结束投票异常");
//        }
//    }
//
//    private boolean queryVote() {
//
//        try {
//
//            synchronized (this) {
//                wait(2500);
//                Map<String, String> map = new HashMap<>();
//                map.put("gameId", gameId);
//                String doPost = HttpUtils.doPost(configMap.get("queryVote"), map);
//                logger.info(memberId + "查询投票结果" + doPost);
//
//
//                JSONObject jsonObject = JSON.parseObject(doPost);
//                String result = jsonObject.getJSONObject("data").getJSONObject("vote").getString("result");
//
//                if (StringUtils.isEmpty(result)) {
//                    return true;
//                }
//                if (result.equals("yes")) {
//                    return true;
//                }
//
//                return false;
//            }
//        } catch (Exception e) {
//            huishou();
//            throw new AnBangException(memberId + "查询投票结果异常");
//        }
//    }
//
//    private void huishou() {
//        terminate = true; //心跳关闭
//        notifyAndRefresh();//父线程唤醒  立刻自动结束
//        close();//client关闭  最后关闭client
//    }
//
//}
