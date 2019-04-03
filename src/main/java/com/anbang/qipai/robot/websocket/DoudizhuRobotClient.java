package com.anbang.qipai.robot.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anbang.qipai.robot.config.PukeUrlConfig;
import com.anbang.qipai.robot.exceptions.AnBangException;
import com.anbang.qipai.robot.utils.HttpUtil;
import com.anbang.qipai.robot.utils.HttpUtils;
import com.anbang.qipai.robot.websocket.vo.doudizhu.DoudizhuPlayerShoupaiVO;
import com.anbang.qipai.robot.websocket.vo.doudizhu.DoudizhuPlayerValueObjectVO;
import com.anbang.qipai.robot.websocket.vo.doudizhu.PanValueObjectVO;
import com.dml.doudizhu.player.action.da.solution.DaPaiDianShuSolution;
import com.dml.puke.pai.DianShu;
import com.dml.puke.pai.PukePai;
import com.dml.puke.pai.PukePaiMian;
import com.dml.puke.wanfa.dianshu.paizu.DianShuZuPaiZu;
import com.dml.puke.wanfa.position.Position;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @Description: 斗地主client
 */
public class DoudizhuRobotClient extends AbstractRobotClient {

    private Position position;

    Gson gson = new Gson();
    Random random = new Random();

    public DoudizhuRobotClient(String url, String robotName, String gameToken, String gameId, String robotId, String memberId) throws URISyntaxException {
        super(url, robotName, gameToken, gameId, robotId, memberId);
        super.configMap = PukeUrlConfig.getDoudizhu();
    }

    @Override
    public void onOpen(ServerHandshake shake) {
        timer();
    }

    @Override
    public void onMessage(String paramString) {
        JSONObject messageObject = JSON.parseObject(paramString);

        // TODO: 2019/4/2
        System.out.println("机器人收到scope" + messageObject);

        if (messageObject.getString("msg").equals("bindPlayer")) {
            bindPlayer();
            // 绑定成功则准备
            ready(gameToken);

        } else if (messageObject.getString("msg").equals("query")) {

            if (messageObject.getJSONObject("data").getString("scope").equals("panForMe")) {
                hasReady = false;
                panForMe();
            } else if (messageObject.getJSONObject("data").getString("scope").equals("panResult")) {
                huishou();
            } else if (messageObject.getJSONObject("data").getString("scope").equals("juResult")) {
                huishou();
            } else if (messageObject.getJSONObject("data").getString("scope").equals("gameFinishVote")) {
                doudizhuFinishVote();

                if (queryVote()) {
                    terminate = true;
                    // 机器人回收
                    huishou();
                }
            }
        }
    }

    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
        logger.info("机器人" + name + "socket关闭...");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        throw new AnBangException(name + "Client遇到异常");
    }

    private void ready(String token) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            Random random = new Random();
            // int rand = random.nextInt(1500);
            // 等待150ms准备
            // synchronized (this) {
            // wait(1000 + rand);
            // }

            String doPost = HttpUtils.doPost(configMap.get("ready"), map);
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

    private void panForMe() {
        try {
            // logger.info("机器人" + name + "请求panforme的参数" + gameMap.toString());
            String doPost = HttpUtils.doPost(configMap.get("panforme"), gameMap);

            System.out.println("jsonjjjjjjjjjjjjjjjjjjj" );
            System.out.println(doPost);

            JSONObject postResult = JSON.parseObject(doPost);

            if (postResult.getString("success").equals("false")) {
                return;
            }

            JSONObject panActionFrame = postResult.getJSONObject("data").getJSONObject("panActionFrame");
            String panAfterAction = panActionFrame.getJSONObject("panAfterAction").toJSONString();

            PanValueObjectVO panValueObject = JSON.parseObject(panAfterAction, PanValueObjectVO.class);

            // 抢地主状态
            List<DoudizhuPlayerValueObjectVO> doudizhuPlayerValueObjects = panValueObject.getDoudizhuPlayerList();
            for(DoudizhuPlayerValueObjectVO list : doudizhuPlayerValueObjects) {
                String playerId = list.getId();

                // 抢地主
                if (memberId.equals(playerId) && list.getState() != null) {
                    String state = list.getState().name();
                    if ("startQiangdizhu".equals(state) || "startJiaodizhu".equals(state)) {
                        Map map = new HashMap();
                        map.put("token", gameToken);
                        map.put("qiang", random.nextBoolean());
                        HttpUtils.doPost(configMap.get("qiangdizhu"), gameMap);
                        return;
                    }
                }
            }

            // 可以看牌打牌了
            String dizhuPlayerId = panValueObject.getDizhuPlayerId();
            String latestDapaiPlayerId = panValueObject.getLatestDapaiPlayerId();

            DoudizhuPlayerValueObjectVO doudizhuPlayer = null;
            for (DoudizhuPlayerValueObjectVO list : doudizhuPlayerValueObjects) {
                String id = list.getId();
                if (id.equals(memberId)) {
                    doudizhuPlayer = list;
                    break;
                }
            }

            // 打牌位置为自己时才打牌
            Position actionPosition = panValueObject.getActionPosition();
            if (actionPosition != null) {
                if (!actionPosition.equals(doudizhuPlayer.getPosition())) {
                    return;
                }
            }

            List<DaPaiDianShuSolution> yaPaiSolutionCandidates = doudizhuPlayer.getYaPaiSolutionCandidates();
            List<DaPaiDianShuSolution> yaPaiSolutionsForTips = doudizhuPlayer.getYaPaiSolutionsForTips();
            List<DianShuZuPaiZu> dachuPaiZuList = panValueObject.getDachuPaiZuList();

            if (CollectionUtils.isEmpty(yaPaiSolutionCandidates)) {
                guo();
            } else {
                DianShuZuPaiZu lastPaiZu = null;
                if (!CollectionUtils.isEmpty(dachuPaiZuList)) {
                    lastPaiZu = dachuPaiZuList.get(dachuPaiZuList.size() - 1);
                }

                // 要不起则不出
                if (CollectionUtils.isEmpty(yaPaiSolutionCandidates)) {
                    guo();
                    return;
                }

                DoudizhuPlayerShoupaiVO allShoupai = doudizhuPlayer.getAllShoupai();
                PlayTypeEnum playType = getPlayType(lastPaiZu, latestDapaiPlayerId, dizhuPlayerId, memberId);
                if (playType.equals(PlayTypeEnum.oneself)) {
                    System.out.println("机器人先打牌:-----" + JSON.toJSONString(yaPaiSolutionCandidates));
                    DaPaiDianShuSolution solutionsForTips = yaPaiSolutionCandidates.get(0);
                    List<Integer> paiIds = getPaiIds(allShoupai.getAllShoupai(), solutionsForTips.getDachuDianShuArray());
                    da(paiIds, solutionsForTips.getDianshuZuheIdx());
                }
                if (playType.equals(PlayTypeEnum.partner)) {
                    DaPaiDianShuSolution solutionsForTips = yaPaiSolutionCandidates.get(0);
                    List<Integer> paiIds = getPaiIds(allShoupai.getAllShoupai(), solutionsForTips.getDachuDianShuArray());
                    da(paiIds, solutionsForTips.getDianshuZuheIdx());
                }
                if (playType.equals(PlayTypeEnum.opponent)) {
                    DaPaiDianShuSolution solutionsForTips = yaPaiSolutionCandidates.get(0);
                    List<Integer> paiIds = getPaiIds(allShoupai.getAllShoupai(), solutionsForTips.getDachuDianShuArray());
                    da(paiIds, solutionsForTips.getDianshuZuheIdx());
                }
            }

        } catch (IOException e) {
            huishou();
            e.printStackTrace();
            throw new AnBangException("获取盘局信息时异常");
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
            HttpResponse post = HttpUtil.doPost(configMap.get("da"), "/pk/da", "POST", headers, querys, json);
            lastChuPaiTime = System.currentTimeMillis();

            Map map = gson.fromJson(EntityUtils.toString(post.getEntity()), Map.class);

            // TODO: 2019/4/3
            System.out.println(memberId + "机器人打牌:" + JSON.toJSONString(paiIds));
            System.out.println(JSON.toJSONString(post));
            System.out.println(JSON.toJSONString(map));

            // 游戏结束，回收机器人
            if (map.toString().contains("panResult")) {
                huishou();
            }

        } catch (Exception e) {
            huishou();
            e.printStackTrace();
            throw new AnBangException(name + "打牌时出现异常");
        }
    }

    private void guo(){
        try {
            synchronized (this) {
                wait(1000);
            }

            Map map = new HashMap();
            map.put("token", gameToken);
            HttpUtils.doPost(configMap.get("guo"), map);
        }catch (IOException e) {
            huishou();
            throw new AnBangException(name + "过时出现异常");
        } catch (InterruptedException exception) {
            huishou();
            throw new AnBangException(name + "等待时候异常");
        }
    }

    private List<Integer> getPaiIds(List<PukePai> allShoupai, DianShu[] dachuDianShuArray) {
        List<Integer> paiIds = new ArrayList<>();

        for (int i = 0; i < dachuDianShuArray.length; i++) {
            for (PukePai list : allShoupai) {
                if (paiIds.contains(list.getId())) {
                    continue;
                }

                PukePaiMian pukePaiMian = list.getPaiMian();
                if (pukePaiMian.dianShu().equals(dachuDianShuArray[i])) {
                    paiIds.add(list.getId());
                    break;
                }
            }
        }
        return paiIds;
    }

    private void doudizhuFinishVote() {
        Map<String, String> map = new HashMap<>();
        map.put("token", gameToken);
        map.put("yes", "true");
        try {
            synchronized (this) {
                wait(1000);
            }
            HttpUtils.doPost(configMap.get("vote"), map);
        } catch (Exception e) {
            huishou();
            e.printStackTrace();
            throw new AnBangException(name + "投票解散房间时异常");
        }
    }

    private boolean queryVote() {
        try {
            synchronized (this) {
                wait(2500);
            }
            Map<String, String> map = new HashMap<>();
            map.put("gameId", gameId);
            String doPost = HttpUtils.doPost(configMap.get("queryvote"), map);
            JSONObject jsonObject = JSON.parseObject(doPost);
            logger.info(jsonObject.toJSONString());
            String result = jsonObject.getJSONObject("data").getJSONObject("vote").getString("result");

            if (StringUtils.isEmpty(result)) {
                return true;
            }
            if (result.equals("yes")) {
                return true;
            }

            return false;
        } catch (Exception e) {
            huishou();
            e.printStackTrace();
            throw new AnBangException(name + "查询投票结果时异常");
        }
    }

    private PlayTypeEnum getPlayType (DianShuZuPaiZu lastPaiZu, String latestDapaiPlayerId, String dizhuPlayerId, String memberId) {
        if (lastPaiZu == null || latestDapaiPlayerId == null || memberId.equals(latestDapaiPlayerId)) {  // 还没人出牌或最后出牌人是自己
            return PlayTypeEnum.oneself;
        }
        if (latestDapaiPlayerId.equals(dizhuPlayerId)) { // 最后出牌人是地主
            return PlayTypeEnum.opponent;
        }
        return PlayTypeEnum.partner;
    }


}
