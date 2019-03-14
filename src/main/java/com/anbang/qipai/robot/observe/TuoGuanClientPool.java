//package com.anbang.qipai.robot.observe;
//
//import com.anbang.qipai.robot.websocket.MajiangTuoGuanClient;
//import com.anbang.qipai.robot.websocket.PukeTuoGuanClient;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Author: 吴硕涵
// * @Date: 2019/3/5 2:40 PM
// * @Version 1.0
// */
//
//
////为了防止在游戏中的token来回变更
//public class TuoGuanClientPool {
//    /*
//    map的Value可以理解为放的是类似于C++地址一样的东西
//    不是值拷贝
//    修改的就是对象的本身
//    * */
//
//    // key——gameId
//    public static Map<String, MajiangTuoGuanClient> majiangTuoGuanClientMap = new HashMap<>();
//
//    public static Map<String, PukeTuoGuanClient> pukeTuoGuanClientMap = new HashMap<>();
//
//    public static void releaseMajiangClient(String gameId) {
//        if (majiangTuoGuanClientMap.containsKey(gameId)) {
//            majiangTuoGuanClientMap.remove(gameId);
//        }
//    }
//
//
//    public static void addMajiangClient(String gameId, MajiangTuoGuanClient client) {
//        majiangTuoGuanClientMap.put(gameId, client);
//    }
//
//    public static void update(String gameId, String playerId, String token) {
//        if (majiangTuoGuanClientMap.containsKey(gameId)) {
//            majiangTuoGuanClientMap.get(gameId).updateToken(playerId,token);
//        }
//    }
//
//}
