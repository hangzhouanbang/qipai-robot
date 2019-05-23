package com.anbang.qipai.robot.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anbang.qipai.robot.config.PukeUrlConfig;
import com.anbang.qipai.robot.exceptions.AnBangException;
import com.anbang.qipai.robot.utils.HttpUtil;
import com.anbang.qipai.robot.utils.HttpUtils;
import com.anbang.qipai.robot.websocket.vo.paodekuai.DaPaiDianShuSolution;
import com.anbang.qipai.robot.websocket.vo.paodekuai.PaodekuaiPanValueObjectVO;
import com.anbang.qipai.robot.websocket.vo.paodekuai.PaodekuaiPlayerValueObjectVO;
import com.dml.puke.pai.DianShu;
import com.dml.puke.pai.PukePai;
import com.dml.puke.pai.PukePaiMian;
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

public class PaodekuaiRobotClient extends AbstractRobotClient {
    public PaodekuaiRobotClient(String url, String robotName, String gameToken, String gameId, String robotId, String memberId) throws URISyntaxException {
        super(url, robotName, gameToken, gameId, robotId, memberId);
        super.configMap = PukeUrlConfig.getDoudizhu();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
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
                finishVote();

                if (queryVote()) {
                    terminate = true;
                    // 机器人回收
                    huishou();
                }
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.info("跑的快机器人" + name + "socket关闭...");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        throw new AnBangException(name + "跑的快Client遇到异常");
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
            String doPost = HttpUtils.doPost(configMap.get("panforme"), gameMap);
            JSONObject postResult = JSON.parseObject(doPost);
            if (postResult.getString("success").equals("false")) {
                return;
            }
            JSONObject panActionFrame = postResult.getJSONObject("data").getJSONObject("panActionFrame");
            String panAfterAction = panActionFrame.getJSONObject("panAfterAction").toJSONString();
            PaodekuaiPanValueObjectVO panValueObjectVO = JSON.parseObject(panAfterAction, PaodekuaiPanValueObjectVO.class);

            // TODO: 2019/5/8
            System.out.println("机器人panActionFrame:" + name);
            System.out.println(panActionFrame);

            // 应打位置
            Position actionPosition = panValueObjectVO.getActionPosition();
            if (actionPosition == null) {
                return;
            }

            // 自己应打，取出牌信息
            PaodekuaiPlayerValueObjectVO playerObject = new PaodekuaiPlayerValueObjectVO();
            List<PaodekuaiPlayerValueObjectVO> paodekuaiPlayerList = panValueObjectVO.getPaodekuaiPlayerList();
            for (PaodekuaiPlayerValueObjectVO list : paodekuaiPlayerList) {
                if (memberId.equals(list.getId())) {
                    if (actionPosition.equals(list.getPosition())) {
                        playerObject = list;
                    }
                    return;
                }
            }

            List<DaPaiDianShuSolution> yaPaiSolution = playerObject.getYaPaiSolutionCandidates();
            List<DaPaiDianShuSolution> yaPaiTips = playerObject.getYaPaiSolutionsForTips();
            playerObject.getAllShoupai();

            if (CollectionUtils.isEmpty(yaPaiSolution)) {
                guo();
            } else {
                // 打牌
                for (int i = 0; i < yaPaiSolution.size(); i++) {
                    DaPaiDianShuSolution daPaiSolution = yaPaiSolution.get(i);

                    // 打牌, 按牌顺序取不考虑首张黑桃三
                    List<Integer> paiIds = getPaiIds(playerObject.getAllShoupai().getAllShoupai(), daPaiSolution.getDachuDianShuArray());
                    da(paiIds, daPaiSolution.getDianshuZuheIdx());
                    return;
                }

            }

        } catch (IOException e) {
            huishou();
            e.printStackTrace();
            throw new AnBangException("跑的快机器人获取盘局信息时异常");
        }
    }


    private void finishVote() {
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

            // 游戏结束，回收机器人
            Map map = gson.fromJson(EntityUtils.toString(post.getEntity()), Map.class);
            if (map.toString().contains("panResult") || map.toString().contains("juResult")) {
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

}
