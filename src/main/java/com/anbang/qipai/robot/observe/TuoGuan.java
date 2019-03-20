package com.anbang.qipai.robot.observe;

import java.util.HashMap;
import java.util.Map;

import com.anbang.qipai.robot.plan.bean.TuoGuanInfo;

/**
 * @Author: 吴硕涵
 * @Date: 2019/3/4 11:31 AM
 * @Version 1.0
 */
//  一局游戏
public class TuoGuan {

    //key playerId,  value token
    public Map<String, String> gameMap = new HashMap<>();

    void add(String playerId, String token) {
        gameMap.put(playerId, token);
    }

    int delete(String playerId) {
        gameMap.remove(playerId);
        return gameMap.size();
    }

    int size() {
        return gameMap.size();
    }

    TuoGuanInfo queryTuoGuan() {
        TuoGuanInfo info = new TuoGuanInfo();
        for (Map.Entry<String, String> entry : gameMap.entrySet()) {
            info.setPlayerId(entry.getKey());
            info.setToken(entry.getValue());
            break;
        }
        return info;
    }
}
