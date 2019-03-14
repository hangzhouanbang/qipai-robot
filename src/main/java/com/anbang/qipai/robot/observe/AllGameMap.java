package com.anbang.qipai.robot.observe;

import com.anbang.qipai.robot.model.TuoGuanInfo;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author: 吴硕涵
 * @Date: 2019/3/4 2:49 PM
 * @Version 1.0
 */

//为每局游戏准备托管
public class AllGameMap {
    private static Logger logger = LoggerFactory.getLogger(AllGameMap.class);
    //good
    //所有的游戏
    //基于LRU策略的  可有效防止MAP的过度增长
    public static ConcurrentLinkedHashMap<String, TuoGuan> allGameMap = new ConcurrentLinkedHashMap.Builder<String, TuoGuan>()
            .maximumWeightedCapacity(600).weigher(Weighers.singleton()).build();


    //正在进行托管的游戏
    public static List<String> register = new LinkedList<>();

    public static void add(String gameId, String playerId, String token) {
        //如果现在有这个GameID的信息
        if (allGameMap.containsKey(gameId)) {
            //token的更新换成client主动拉取的模式
            allGameMap.get(gameId).add(playerId, token);
        } else {
            TuoGuan tuoGuan = new TuoGuan();
            tuoGuan.add(playerId, token);
            allGameMap.put(gameId, tuoGuan);
        }
    }


    //一一排除该局的活人
    public static int removeOne(String gameId, String playerId) {
        if (allGameMap.containsKey(gameId)) {
            if (allGameMap.get(gameId).gameMap.containsKey(playerId)) {
                return allGameMap.get(gameId).delete(playerId);
            }
        }
        return 0;
    }

    //找到被托管的人的INFO
    public static TuoGuanInfo findTuoGuan(String gameId) {
        if (allGameMap.containsKey(gameId)) {
            return allGameMap.get(gameId).queryTuoGuan();
        }
        return null;
    }

    //找到更新过后的Token
    public static String findUpdatedToken(String gameId, String playerId) {
        if (allGameMap.containsKey(gameId)) {
            return allGameMap.get(gameId).gameMap.get(playerId);
        }
        return null;
    }


    public static void regisiterTuoGuan(String gameId) {
        if (!register.contains(gameId)) {
            register.add(gameId);
        }

        //删500个
        if (register.size() > 600) {
            for (int i = 0; i < 500; i++) {
                register.remove(0);
            }
        }
    }

    public static boolean hasRegister(String gameId) {
        return register.contains(gameId);
    }

    public static boolean containsGame(String gameId) {
        return allGameMap.containsKey(gameId);
    }

    public static void deleteGame(String gameId) {
        if (allGameMap.containsKey(gameId)) {
            allGameMap.remove(gameId);
        }

        if (register.contains(gameId)) {
            register.remove(gameId);
        }
    }

}
