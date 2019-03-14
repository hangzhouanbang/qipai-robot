package com.anbang.qipai.robot.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anbang.qipai.robot.config.UrlConfig;
import com.anbang.qipai.robot.dao.dataObject.RobotMemberDbo;
import com.anbang.qipai.robot.enums.PaiXing;
import com.anbang.qipai.robot.exceptions.AnBangException;
import com.anbang.qipai.robot.model.CommonModel;
import com.anbang.qipai.robot.model.Robots;
import com.anbang.qipai.robot.model.ShuangKouPlayerDa;
import com.anbang.qipai.robot.solution.DaPaiDianShuSolution;
import com.anbang.qipai.robot.solution.DianShu;
import com.anbang.qipai.robot.thread.ConditionPool;
import com.anbang.qipai.robot.utils.HttpUtil;
import com.anbang.qipai.robot.utils.HttpUtils;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 1:13 PM
 * @Version 1.0
 */

//双扣机器人客户端
public class RobotClient extends WebSocketClient {

    Logger logger = LoggerFactory.getLogger(RobotClient.class);
//    public final long Eight_Second = 8 * 1000L;


    private String robotId;
    private String wsUrl;
    private String name;
    private String gameToken;
    private String gameId;
    private String memberId;

    private String position;
    private int positionIndex;

    private long readyTime;
    private boolean hasReady;
    private long lastChuPaiTime;

    private boolean terminate = false;

    private Map<String, String> gameMap;


    public RobotClient(String url, String robotName, String gameToken, String gameId, String robotId, String memberId) throws URISyntaxException {
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

        hasReady = false;
        this.lastChuPaiTime = System.currentTimeMillis();

    }


    //panResult 本盘结束
//    juResult 结束 准备推出
//    gameFinishVote --/game/vote_to_f  yes -true
    //game info
    //准备下一盘 ready_to_nex
    @Override
    public void onOpen(ServerHandshake shake) {
        logger.info("机器人" + name + "建立三次握手");
        timer();
        logger.info("机器人" + name + "定时任务开始");
        logger.info("机器人" + name + "memberId" + memberId);

    }


    @Override
    public void onMessage(String paramString) {
        logger.info("机器人" + name + "接收到消息：" + paramString);
        JSONObject messageObject = JSON.parseObject(paramString);

        if (messageObject.getString("msg").equals("bindPlayer")) {
            bindPlayer();
            //绑定成功 则准备

        } else if (messageObject.getString("msg").equals("query")) {

            if (messageObject.getJSONObject("data").getString("scope").equals("panForMe")) {
                hasReady = false;
                panForMe();
            } else if (messageObject.getJSONObject("data").getString("scope").equals("panResult")) {
                readyToNextShuangKou(gameToken);
            } else if (messageObject.getJSONObject("data").getString("scope").equals("juResult")) {
                huishou();
            } else if (messageObject.getJSONObject("data").getString("scope").equals("gameFinishVote")) {
                shuangkouFinishVote();

                if (queryVote()) {
                    terminate = true;
                    //机器人回收
                    huishou();
                }
            }

        }
    }

//    private void notifyAndRefresh() {
////        唤醒
////        ConditionPool.reentrantLockMap.get(fatherConditionId).lock();
//        ConditionPool.conditionMap.get(fatherConditionId).signalAll();
//
//        ConditionPool.removeCondition(fatherConditionId);
//        ConditionPool.removeLock(fatherConditionId);
//    }

    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
        logger.info("机器人" + name + "关闭...");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        throw new AnBangException(name + "Client遇到异常");
    }

    private void bindPlayer() {
        CommonModel model = new CommonModel();
        model.setMsg("bindPlayer");
        model.setData(gameMap);
        send(JSON.toJSONString(model));
        logger.info(name + "绑定玩家成功");
        ready(gameToken);
    }

    public void emergency(JSONObject shuangkouPlayerListOne) {
        logger.info(name + "没有提示的异常情况" + shuangkouPlayerListOne);
        JSONArray shouPai = shuangkouPlayerListOne.getJSONObject("allShoupai").getJSONArray("allShoupai");
        if (shouPai == null) {
            ;
        }
        if (shouPai.size() == 0) {
            return;
        }

        int min = 500; //最小的牌的ID

        //treeMap默认排序
        TreeMap<Integer, String> paiMap = new TreeMap<>();
        for (int index = 0; index < shouPai.size(); ++index) {
            paiMap.put(Integer.parseInt(shouPai.getJSONObject(index).getString("id")),
                    shouPai.getJSONObject(index).getString("paiMian"));

            if (Integer.parseInt(shouPai.getJSONObject(index).getString("id")) < min) {
                min = Integer.parseInt(shouPai.getJSONObject(index).getString("id"));
            }
        }

        String value = paiMap.get(min);
        String paimian = value;

        for (PaiXing e : PaiXing.values()) {
            if (value.contains(e.name())) {
                paimian = value.substring(e.name().length(), value.length());
            }
            //san si dawang
            //此时大王没有被截掉
        }

        //将要出牌的List
        //排在最前的是最优的
        List<Integer> paiIds = new ArrayList<>();
        List<String> paiMians = new ArrayList<>();

        //如果最小的不是大王  牌面为san--K

        for (Map.Entry<Integer, String> entry : paiMap.entrySet()) {
            if (entry.getValue().contains(paimian)) {
                paiIds.add(entry.getKey());
                paiMians.add(entry.getValue());
            } else {
                break;
            }
        }


        String xiaowang = hasXiaoWang(shouPai);
        if (!StringUtils.isEmpty(xiaowang) && !value.equals("dawang") && paiIds.size() >= 5) {
            paiIds.add(Integer.parseInt(xiaowang));
            paiMians.add(paiMap.get(Integer.parseInt(xiaowang)));
        }
        //是否带王   5线以上带王
        String dawang = hasDaWang(shouPai);
        if (!StringUtils.isEmpty(dawang) && !value.equals("xiaowang") && paiIds.size() >= 5) {
            paiIds.add(Integer.parseInt(dawang));
            paiMians.add(paiMap.get(Integer.parseInt(dawang)));
        }


        String dianshuZuheIdx = null;
        //不可能为0
        DianShu[] dianShus = new DianShu[paiIds.size()];

        //要出多少张牌
        for (int index = 0; index < paiIds.size(); ++index) {
            for (DianShu e : DianShu.values()) {
                if (paiMians.get(index).contains(e.name())) {
                    dianShus[index] = e;
                    break;
                }
            }
        }
        logger.info(name + "其牌面的数组是" + paiMians);

        logger.info(name + "去计算点数组的数组为" + dianShus[0]);
        dianshuZuheIdx = DaPaiDianShuSolution.calculateDianshuZuheIdx(dianShus);
        logger.info(name + "没有提示然后打出" + paiIds);
        da(paiIds, dianshuZuheIdx);
    }

    private void panForMe() {
        try {
//            logger.info("机器人" + name + "请求panforme的参数" + gameMap.toString());
            String doPost = HttpUtils.doPost(UrlConfig.getPanformeUrl(), gameMap);

            JSONObject postResult = JSON.parseObject(doPost);

            if (postResult.getString("success").equals("false")) {
                if (postResult.getString("msg").equals("game not playing")) {
                    huishou();
                    return;
                }
            }

            JSONObject panAfterAction = postResult.getJSONObject("data")
                    .getJSONObject("panActionFrame")
                    .getJSONObject("panAfterAction");

            //不是自己的  拉了panforme 没有用
            if (!StringUtils.isEmpty(position)) {
                if (!panAfterAction.getString("actionPosition").equals(position)) {
                    return;
                }
            }

//            logger.info(name + "获取panforme" + doPost);


            JSONArray shuangkouPlayerList = panAfterAction.getJSONArray("shuangkouPlayerList");


            JSONObject shuangkouPlayerListOne = null;
            for (int i = 0; i < shuangkouPlayerList.size(); i++) {
                if (shuangkouPlayerList.getJSONObject(i).getString("id").equals(memberId)) {
                    shuangkouPlayerListOne = shuangkouPlayerList.getJSONObject(i);
                    if (StringUtils.isEmpty(position)) {
                        position = shuangkouPlayerList.getJSONObject(i).getString("position");
                        if (!panAfterAction.getString("actionPosition").equals(position)) {
                            return;
                        }
                    }
                    break;
                }
            }

            JSONArray shouPai = shuangkouPlayerListOne.getJSONObject("allShoupai").getJSONArray("allShoupai");

            Map<String, String> paiMap = new HashMap<>();
            //更新手牌MAP
            for (int i = 0; i < shouPai.size(); i++) {
                JSONObject pai = shouPai.getJSONObject(i);
                paiMap.put(pai.getString("id"), pai.getString("paiMian"));
            }

            JSONArray yapaiSollution = shuangkouPlayerListOne.getJSONArray("yaPaiSolutionCandidates");
            String otherguo = shuangkouPlayerListOne.getString("guo");

//            logger.info(name + "tipsArray的结果为" + tipsArray);

            if (yapaiSollution == null) {
                return;
            }


            if (yapaiSollution.size() == 0) {
                guo(shuangkouPlayerListOne);
            } else {
                //是否压对家
                if (isGuo(shuangkouPlayerList)) {
                    guo(shuangkouPlayerListOne);
                    return;
                }

                JSONArray tipsArray = shuangkouPlayerListOne.getJSONArray("yaPaiSolutionsForTips");

                //防止最后一张牌不出
                if (tipsArray.size() == 0) {
                    emergency(shuangkouPlayerListOne);
                    return;
                }

                String dianshuZuheIdx = null;
                JSONArray dachuDianShuArray = null;

                if (tipsArray.size() <= 3 && isdaiwang(tipsArray)) {
                    dianshuZuheIdx = tipsArray.getJSONObject(tipsArray.size() - 1).getString("dianshuZuheIdx");
                    dachuDianShuArray = tipsArray.getJSONObject(tipsArray.size() - 1).getJSONArray("dachuDianShuArray");
                } else {
                    dianshuZuheIdx = tipsArray.getJSONObject(0).getString("dianshuZuheIdx");
                    dachuDianShuArray = tipsArray.getJSONObject(0).getJSONArray("dachuDianShuArray");
                }

                List<Integer> paiIds = new ArrayList<>();

                for (int i = 0; i < dachuDianShuArray.size(); i++) {
                    String value = dachuDianShuArray.getString(i);

                    boolean a = false;//没有找到
                    String key = null;
                    for (Map.Entry<String, String> entry : paiMap.entrySet()) {
                        if (entry.getValue().contains(value)) {
                            key = entry.getKey();
                            paiIds.add(new Integer(Integer.parseInt(key)));
                            a = true;
                            break;
                        }
                    }
                    if (a == true) {
                        paiMap.remove(key);
                    }
                }
                da(paiIds, dianshuZuheIdx);

            }
        } catch (IOException e) {
            huishou();
            e.printStackTrace();
            throw new AnBangException("获取盘局信息时异常");
        }
    }


    public void guo(JSONObject shuangkouPlayerList) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("token", gameToken);
            synchronized (this) {
                wait(1000);
            }

            String doPost = HttpUtils.doPost(UrlConfig.getGuoUrl(), map);
            logger.info(name + "请求过牌" + doPost);
            if (JSON.parseObject(doPost).getString("success").equals("false")) {
                emergency(shuangkouPlayerList);
                return;
            } else if (JSON.parseObject(doPost).getJSONObject("data").getJSONArray("queryScopes").contains("panForMe")) {
                panForMe();
                return;
            }
            lastChuPaiTime = System.currentTimeMillis();
            return;
        } catch (IOException e) {
            huishou();
            throw new AnBangException(name + "过时出现异常");
        } catch (InterruptedException exception) {
            huishou();
            throw new AnBangException(name + "等待时候异常");
        }
    }


    public void da(List<Integer> paiIds, String dianshuZuheIdx) {
        try {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            Map<String, String> querys = new HashMap<String, String>();
            Map data = new HashMap<>();

            querys.put("token", gameToken);
            querys.put("dianshuZuheIdx", dianshuZuheIdx);

            Gson gson = new Gson();
            String json = gson.toJson(paiIds);
            data.put("paiIds", json);

            Random random = new Random();
            int rand = random.nextInt(1500) + 800;
            long timeout = (int) rand;
            synchronized (this) {
                wait(timeout);
            }
            HttpResponse post = HttpUtil.doPost(UrlConfig.getDaUrl(), "/pk/da", "POST", headers, querys, json);

            logger.info(name + "出牌" + post);
            lastChuPaiTime = System.currentTimeMillis();

            Map map = gson.fromJson(EntityUtils.toString(post.getEntity()), Map.class);

            if (map.toString().contains("panResult")) {
                //准备下一盘
                readyToNextShuangKou(gameToken);
            }

        } catch (Exception e) {
            huishou();
            e.printStackTrace();
            throw new AnBangException(name + "打牌时出现异常");
        }
    }

    private void readyToNextShuangKou(String token) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        try {
            String doPost = HttpUtils.doPost(UrlConfig.getShuangkouReadyNext(), map);
            logger.info(name + "第二盘" + doPost);
        } catch (IOException e) {
            huishou();
            e.printStackTrace();
            throw new AnBangException(name + "准备下一盘麻将时异常");
        }
    }

    private void shuangkouFinishVote() {
        Map<String, String> map = new HashMap<>();
        map.put("token", gameToken);
        map.put("yes", "true");
        try {
            synchronized (this) {
                wait(1000);
            }
            HttpUtils.doPost(UrlConfig.getShuangkouVote(), map);
        } catch (Exception e) {
            huishou();
            e.printStackTrace();
            throw new AnBangException(name + "投票解散房间时异常");
        }
    }

    private void huishou() {
        terminate = true;
        //准备撤退
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
    }

    private boolean queryVote() {
        try {
            synchronized (this) {
                wait(2500);
            }
            Map<String, String> map = new HashMap<>();
            map.put("gameId", gameId);
            String doPost = HttpUtils.doPost(UrlConfig.getShuangkouQueryVoteUrl(), map);
            JSONObject jsonObject = JSON.parseObject(doPost);
            logger.info(jsonObject.toJSONString());
            String result = jsonObject.getJSONObject("data").getJSONObject("vote").getString("result");

            if (StringUtils.isEmpty(result)) {
                return true;
            }
            if (result.equals("yes")) {
                logger.info(name + "游戏已经解散");
                return true;
            }

            return false;
        } catch (Exception e) {
            huishou();
            e.printStackTrace();
            throw new AnBangException(name + "查询投票结果时异常");
        }
    }


    private void ready(String token) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            Random random = new Random();
//            int rand = random.nextInt(1500);
            //等待150ms准备
//            synchronized (this) {
//                wait(1000 + rand);
//            }

            String doPost = HttpUtils.doPost(UrlConfig.getReadyUrl(), map);
            logger.info("机器人" + name + "准备" + doPost);
            if (JSON.parseObject(doPost).getJSONObject("data").getJSONArray("queryScopes").contains("panForMe")) {
                panForMe();
            }

            hasReady = true;
            readyTime = System.currentTimeMillis();
        } catch (IOException e) {
            huishou();
            throw new AnBangException("准备时异常");
        } catch (Exception e) {

        }
    }
//
//    private boolean queryGameInfo() {
//        Map<String, String> map = new HashMap<>();
//        map.put("gameId", gameId);
//        try {
//            String s = HttpUtils.doPost(UrlConfig.getGameInfoUrl(), map);
//            logger.info("aaaa"+s);
//        } catch (IOException e) {
//            throw new AnBangException(name+"获取房间信息时出错");
//        }
//        return true;
//    }


    public void sendHeartBeat() {
        //发送心跳的时候如果长时间不准备 机器人自己离开房间
        if (hasReady) {
            if (System.currentTimeMillis() - readyTime > 15 * 1000) {
                huishou();
                leaveRoom();
            }
        }

        if (System.currentTimeMillis() - lastChuPaiTime > 75 * 1000) {
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
//            logger.info("机器人" + name + "发送心跳");
        }
    }


    //线程级别的定时任务
    public void timer() {
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.HOUR_OF_DAY, 10); // 控制时
//        c.set(Calendar.MINUTE, 0); // 控制分
//        c.set(Calendar.SECOND, 0); // 控制秒
//
//        Date time = c.getTime(); // 得到执行任务的时间,此处为当天的10：00：00

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
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

    //TODO:  反复离开
    private void faQiJieSan() {
        Map<String, String> map = new HashMap<>();
        map.put("token", gameToken);
        try {
            HttpUtils.doPost(UrlConfig.getJiesanUrl(), map);
            logger.info("机器人" + name + "发起解散游戏");
        } catch (Exception e) {
            logger.error("机器人" + name + "发起解散游戏异常");
        }
    }

    private void leaveRoom() {
        Map<String, String> map = new HashMap<>();
        map.put("token", gameToken);
        logger.info(name + "离开游戏");
        try {
            HttpUtils.doPost(UrlConfig.getLeaveGame(), map);
        } catch (Exception e) {
            logger.error("机器人" + name + "离开游戏异常");
        }
    }


    private boolean isGuo(JSONArray shuangkouPlayerList) {

//        logger.info("双扣isGuo收到" + shuangkouPlayerList + "memberId:" + memberId);

        if (shuangkouPlayerList.size() != 4) {
            return false;
        }

        ShuangKouPlayerDa[] playerDas = new ShuangKouPlayerDa[4];

        //按照东南西北重新排序
        int index = 0;
        int memberIndex = 0;


        for (index = 0; index < shuangkouPlayerList.size(); index++) {
            ShuangKouPlayerDa playerDa = new ShuangKouPlayerDa();

            String position = shuangkouPlayerList.getJSONObject(index).getString("position");


            playerDa.setPosition(position);

            String playerId = shuangkouPlayerList.getJSONObject(index).getString("id");
            playerDa.setMemberId(playerId);
            playerDa.setJson(shuangkouPlayerList.getJSONObject(index).getJSONObject("publicDachuPaiZu"));


            if (position.equals("dong")) {
                if (playerId.equals(memberId)) {
                    positionIndex = 0;
                    memberIndex = index;
                }
                playerDas[0] = playerDa;
            } else if (position.equals("nan")) {
                if (playerId.equals(memberId)) {
                    positionIndex = 1;
                    memberIndex = index;
                }
                playerDas[1] = playerDa;
            } else if (position.equals("xi")) {
                if (playerId.equals(memberId)) {
                    positionIndex = 2;
                    memberIndex = index;
                }
                playerDas[2] = playerDa;
            } else if (position.equals("bei")) {
                if (playerId.equals(memberId)) {
                    positionIndex = 3;
                    memberIndex = index;
                }
                playerDas[3] = playerDa;
            }


        }


        int duijiaIndex = positionIndex + 2;
        if (duijiaIndex >= 4) {
            duijiaIndex = duijiaIndex - 4;
        }


        JSONObject dachuPaiZuDuijia = playerDas[duijiaIndex].getJson();
        if (dachuPaiZuDuijia == null) {
            return false;   //对家不出牌就不过
        } else {
            //对家出牌了
//            logger.info(name + "position" + positionIndex + "的对家" + duijiaIndex + "打出牌组" + dachuPaiZuDuijia);
        }


        int duijiaNextIndex = duijiaIndex + 1;
        if (duijiaNextIndex >= 4) {
            duijiaNextIndex = duijiaNextIndex - 4;
        }

        JSONObject dachuPaiZuDuiNext = playerDas[duijiaNextIndex].getJson();
//        logger.info(name + "的对家的下家打出牌组" + dachuPaiZuDuiNext);


        if (dachuPaiZuDuiNext == null) {
//            logger.info(name+"有没有炸弹"+shuangkouPlayerList.getJSONObject(memberIndex).getJSONArray("yaPaiSolutionsForTips")
//                    .getJSONObject(0).getJSONObject("dianShuZu").getString("size"));

            //true 没有炸弹
            boolean isZhaDan = StringUtils.isEmpty(shuangkouPlayerList.getJSONObject(memberIndex).getJSONArray("yaPaiSolutionsForTips")
                    .getJSONObject(0).getJSONObject("dianShuZu").getString("size"));
            if (isZhaDan) {
                return false;//不过
            } else {
                return true; //过
            }
        } else {
            return false;  //对家出牌 别人也出牌 自己也出牌
        }
    }


    String hasDaWang(JSONArray array) {
        for (int index = 0; index < array.size(); index++) {
            if (array.getJSONObject(index).getString("paiMian").equals("dawang")) {
                return array.getJSONObject(index).getString("id");
            }
        }
        return null;
    }

    String hasXiaoWang(JSONArray array) {
        for (int index = 0; index < array.size(); index++) {
            if (array.getJSONObject(index).getString("paiMian").equals("xiaowang")) {
                return array.getJSONObject(index).getString("id");
            }
        }
        return null;
    }

    boolean isdaiwang(JSONArray array) {
        for (int index = 0; index < array.size(); index++) {
            if (array.getJSONObject(index).getJSONArray("dachuDianShuArray").contains("dawang") ||
                    array.getJSONObject(index).getJSONArray("dachuDianShuArray").contains("xiaowang")) {
                return true;
            }
        }
        return false;
    }
}


