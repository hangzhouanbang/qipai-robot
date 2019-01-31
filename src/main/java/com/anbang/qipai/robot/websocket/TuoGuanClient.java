package com.anbang.qipai.robot.websocket;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 1:14 PM
 * @Version 1.0
 */
//public class TuoGuanClient extends WebSocketClient {
//    Logger logger = LoggerFactory.getLogger(TuoGuanClient.class);
//
//    private String wsUrl;
//    private String gameToken;
//    private String gameId;
//    private String memberId;
//
//    private int positionIndex;
//
//    private boolean terminate = false;
//
//    private String fatherConditionId;
//
//    private Map<String, String> gameMap;
//
//    public TuoGuanClient(String url, String gameToken, String gameId, String memberId, String conditionId) throws URISyntaxException {
//        super(new URI(url));
//        this.wsUrl = url;
//        this.gameToken = gameToken;
//        this.gameId = gameId;
//        this.memberId = memberId;
//        gameMap = new HashMap<>();
//        gameMap.put("token", gameToken);
//        gameMap.put("gameId", gameId);
//
//        this.fatherConditionId = conditionId;
//    }
//
//    @Override
//    public void onOpen(ServerHandshake shake) {
//        logger.info("托管用户" + memberId + "建立三次握手");
//        timer();
//        logger.info("托管用户" + memberId + "定时任务开始");
//    }
//
//
//
//    @Override
//    public void onMessage(String paramString) {
//        logger.info("托管用户" + memberId + "接收到消息：" + paramString);
//        JSONObject messageObject = JSON.parseObject(paramString);
//
//        if (messageObject.getString("msg").equals("bindPlayer")) {
//           //不可能绑定玩家
//
//        } else if (messageObject.getString("msg").equals("query")) {
//
//            if (messageObject.getJSONObject("data").getString("scope").equals("panForMe")) {
//                panForMe();
//            } else if (messageObject.getJSONObject("data").getString("scope").equals("panResult")) {
//                readyToNextShuangKou(gameToken);
//            } else if (messageObject.getJSONObject("data").getString("scope").equals("juResult")) {
//
//                huishou();
//
//            } else if (messageObject.getJSONObject("data").getString("scope").equals("gameFinishVote")) {
//                shuangkouFinishVote();
//
//                if (queryVote()) {
//                huishou();
//                }
//            }
//
//        }
//    }
//
//
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
////    private void bindPlayer() {
////        CommonModel model = new CommonModel();
////        model.setMsg("bindPlayer");
////        model.setData(gameMap);
////        send(JSON.toJSONString(model));
////        logger.info(memberId + "绑定玩家成功");
////        ready(gameToken);
////    }
//
//
//    public void emergency(JSONObject shuangkouPlayerListOne) {
//        logger.info(memberId + "没有提示的异常情况" + shuangkouPlayerListOne);
//        JSONArray shouPai = shuangkouPlayerListOne.getJSONObject("allShoupai").getJSONArray("allShoupai");
//        String paiMian = shouPai.getJSONObject(0).getString("paiMian");
//        List<Integer> paiIds = new ArrayList<>();
//        paiIds.add(Integer.parseInt(shouPai.getJSONObject(0).getString("id")));
//        String dianshuZuheIdx = null;
//        for (DianShu e : DianShu.values()) {
//            if (paiMian.contains(e.name())) {
//                DianShu[] dianShus = new DianShu[1];
//                dianShus[0] = e;
//                dianshuZuheIdx = DaPaiDianShuSolution.calculateDianshuZuheIdx(dianShus);
//                break;
//            }
//        }
//        da(paiIds, dianshuZuheIdx);
//    }
//
//    private void panForMe() {
//        try {
//            logger.info("托管用户" + memberId + "请求panforme的参数" + gameMap.toString());
//            String doPost = HttpUtils.doPost(UrlConfig.getPanformeUrl(), gameMap);
//
//            logger.info("托管用户" + memberId + "获取panforme" + doPost);
//
//            JSONObject postResult = JSON.parseObject(doPost);
//            JSONArray shuangkouPlayerList = postResult.getJSONObject("data")
//                    .getJSONObject("panActionFrame")
//                    .getJSONObject("panAfterAction")
//                    .getJSONArray("shuangkouPlayerList");
//
//
//            JSONObject shuangkouPlayerListOne = null;
//            for (int i = 0; i < shuangkouPlayerList.size(); i++) {
//                if (shuangkouPlayerList.getJSONObject(i).getString("id").equals(memberId)) {
//                    shuangkouPlayerListOne = shuangkouPlayerList.getJSONObject(i);
//                    break;
//                }
//            }
//
//            JSONArray shouPai = shuangkouPlayerListOne.getJSONObject("allShoupai").getJSONArray("allShoupai");
//
//            Map<String, String> paiMap = new HashMap<>();
//            //更新手牌MAP
//            for (int i = 0; i < shouPai.size(); i++) {
//                JSONObject pai = shouPai.getJSONObject(i);
//                paiMap.put(pai.getString("id"), pai.getString("paiMian"));
//            }
//
//            JSONArray tipsArray = shuangkouPlayerListOne.getJSONArray("yaPaiSolutionsForTips");
//            String otherguo = shuangkouPlayerListOne.getString("guo");
//
////            logger.info(name + "tipsArray的结果为" + tipsArray);
//
//            if (tipsArray == null) {
//                return;
//            }
//
//
//            if (tipsArray.size() == 0) {
//                if (otherguo.equals("true")) {
//                    emergency(shuangkouPlayerListOne);
//                    return;
//                } else {
//                    guo();
//                }
//            } else {
//                if (isGuo(shuangkouPlayerList)) {
//                    guo();
//                    return;
//                }
//
//                String dianshuZuheIdx = tipsArray.getJSONObject(0).getString("dianshuZuheIdx");
//                JSONArray dachuDianShuArray = tipsArray.getJSONObject(0).getJSONArray("dachuDianShuArray");
//                List<Integer> paiIds = new ArrayList<>();
//
//                for (int i = 0; i < dachuDianShuArray.size(); i++) {
//                    String value = dachuDianShuArray.getString(i);
//
//                    boolean a = false;//没有找到
//                    String key = null;
//                    for (Map.Entry<String, String> entry : paiMap.entrySet()) {
//                        if (entry.getValue().contains(value)) {
//                            key = entry.getKey();
//                            paiIds.add(new Integer(Integer.parseInt(key)));
//                            a = true;
//                            break;
//                        }
//                    }
//                    if (a == true) {
//                        paiMap.remove(key);
//                    }
//                }
//                da(paiIds, dianshuZuheIdx);
//            }
//        } catch (IOException e) {
//            huishou();
//            e.printStackTrace();
//            throw new AnBangException("获取盘局信息时异常");
//        }
//    }
//
//
//    public void guo() {
//        try {
//            Map<String, String> map = new HashMap<>();
//            map.put("token", gameToken);
//            HttpUtils.doPost(UrlConfig.getGuoUrl(), map);
//            return;
//        } catch (IOException e) {
//            huishou();
//            throw new AnBangException(memberId + "过时出现异常");
//        }
//    }
//
//    public void da(List<Integer> paiIds, String dianshuZuheIdx) {
//        try {
//            Map<String, String> headers = new HashMap<String, String>();
//            headers.put("Content-Type", "application/json");
//            Map<String, String> querys = new HashMap<String, String>();
//            Map data = new HashMap<>();
//
//            querys.put("token", gameToken);
//            querys.put("dianshuZuheIdx", dianshuZuheIdx);
//
//            Gson gson = new Gson();
//            String json = gson.toJson(paiIds);
//            data.put("paiIds", json);
//
//            Random random = new Random();
//            int rand = random.nextInt(2800) + 10;
//            long timeout = (int) rand;
//            synchronized (this) {
//                wait(timeout);
//            }
//            HttpResponse post = HttpUtil.doPost(UrlConfig.getDaUrl(), "/pk/da", "POST", headers, querys, json);
//
//            Map map = gson.fromJson(EntityUtils.toString(post.getEntity()), Map.class);
//
//            if (map.toString().contains("panResult")) {
//                //准备下一盘
//                readyToNextShuangKou(gameToken);
//            }
//
//        } catch (Exception e) {
//            huishou();
//            e.printStackTrace();
//            throw new AnBangException(memberId + "打牌时出现异常");
//        }
//    }
//
//    private void readyToNextShuangKou(String token) {
//        Map<String, String> map = new HashMap<>();
//        map.put("token", token);
//        try {
//            String doPost = HttpUtils.doPost(UrlConfig.getShuangkouReadyNext(), map);
//            logger.info(memberId + "第二盘" + doPost);
//        } catch (IOException e) {
//            huishou();
//            e.printStackTrace();
//            throw new AnBangException(memberId + "准备下一盘麻将时异常");
//        }
//    }
//
//    private void shuangkouFinishVote() {
//        Map<String, String> map = new HashMap<>();
//        map.put("token", gameToken);
//        map.put("yes", "true");
//        try {
//            synchronized (this) {
//                wait(1000);
//            }
//            HttpUtils.doPost(UrlConfig.getShuangkouVote(), map);
//        } catch (Exception e) {
//            huishou();
//            e.printStackTrace();
//            throw new AnBangException(memberId + "投票解散房间时异常");
//        }
//    }
//
//    private boolean queryVote() {
//        try {
//            synchronized (this) {
//                wait(2500);
//            }
//            Map<String, String> map = new HashMap<>();
//            map.put("gameId", gameId);
//            String doPost = HttpUtils.doPost(UrlConfig.getShuangkouQueryVoteUrl(), map);
//            JSONObject jsonObject = JSON.parseObject(doPost);
//            String result = jsonObject.getJSONObject("data").getJSONObject("vote").getString("result");
//
//            if (StringUtils.isEmpty(result)) {
//                return true;
//            }
//            if (result.equals("yes")) {
//                logger.info(memberId + "游戏已经解散");
//                return true;
//            }
//
//            return false;
//        } catch (Exception e) {
//            huishou();
//            e.printStackTrace();
//            throw new AnBangException(memberId + "查询投票结果时异常");
//        }
//    }
//
//    public void sendHeartBeat() {
//
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
//    //线程级别的定时任务
//    public void timer() {
//
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
//    private boolean isGuo(JSONArray shuangkouPlayerList) {
//        ShuangKouPlayerDa[] playerDas = new ShuangKouPlayerDa[4];
//
//        //按照东南西北重新排序
//        int index = 0;
//        int memberIndex = 0;
//        for (index = 0; index < shuangkouPlayerList.size(); index++) {
//            ShuangKouPlayerDa playerDa = new ShuangKouPlayerDa();
//            String position = shuangkouPlayerList.getJSONObject(index).getString("position");
//            playerDa.setPosition(position);
//
//            String playerId = shuangkouPlayerList.getJSONObject(index).getString("id");
//            playerDa.setMemberId(playerId);
//            playerDa.setJson(shuangkouPlayerList.getJSONObject(index).getJSONObject("publicDachuPaiZu"));
//
//
//            if (position.equals("dong")) {
//                if (playerId.equals(memberId)) {
//                    positionIndex = 0;
//                    memberIndex = index;
//                }
//                playerDas[0] = playerDa;
//            } else if (position.equals("nan")) {
//                if (playerId.equals(memberId)) {
//                    positionIndex = 1;
//                    memberIndex = index;
//                }
//                playerDas[1] = playerDa;
//            } else if (position.equals("xi")) {
//                if (playerId.equals(memberId)) {
//                    positionIndex = 2;
//                    memberIndex = index;
//                }
//                playerDas[2] = playerDa;
//            } else if (position.equals("bei")) {
//                if (playerId.equals(memberId)) {
//                    positionIndex = 3;
//                    memberIndex = index;
//                }
//                playerDas[3] = playerDa;
//            }
//        }
//
//
//        int duijiaIndex = positionIndex + 2;
//        if (duijiaIndex >= 4) {
//            duijiaIndex = duijiaIndex - 4;
//        }
//
//
//        JSONObject dachuPaiZuDuijia = shuangkouPlayerList.getJSONObject(duijiaIndex).getJSONObject("publicDachuPaiZu");
//        if (dachuPaiZuDuijia == null) {
//            return false;   //对家不出牌就不过
//        } else {
//            //对家出牌了
////            logger.info(name + "position" + positionIndex + "的对家" + duijiaIndex + "打出牌组" + dachuPaiZuDuijia);
//        }
//
//
//        int duijiaNextIndex = duijiaIndex + 1;
//        if (duijiaNextIndex >= 4) {
//            duijiaNextIndex = duijiaNextIndex - 4;
//        }
//
//        JSONObject dachuPaiZuDuiNext = shuangkouPlayerList.getJSONObject(duijiaNextIndex).getJSONObject("publicDachuPaiZu");
////        logger.info(name + "的对家的下家打出牌组" + dachuPaiZuDuiNext);
//
//
//        if (dachuPaiZuDuiNext == null) {
////            logger.info(name+"有没有炸弹"+shuangkouPlayerList.getJSONObject(memberIndex).getJSONArray("yaPaiSolutionsForTips")
////                    .getJSONObject(0).getJSONObject("dianShuZu").getString("size"));
//
//            //true 没有炸弹
//            boolean isZhaDan = StringUtils.isEmpty(shuangkouPlayerList.getJSONObject(memberIndex).getJSONArray("yaPaiSolutionsForTips")
//                    .getJSONObject(0).getJSONObject("dianShuZu").getString("size"));
//            if (isZhaDan) {
//                return false;//不过
//            } else {
//                return true; //过
//            }
//        } else {
//            return false;  //对家出牌 别人也出牌 自己也出牌
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
