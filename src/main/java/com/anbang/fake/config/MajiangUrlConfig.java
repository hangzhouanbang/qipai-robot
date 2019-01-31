package com.anbang.fake.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/22 11:07 AM
 * @Version 1.0
 */
public class MajiangUrlConfig {
    private static Map<String, String> wenzhouMajiang;
    private static Map<String, String> ruianMajiang;
    private static Map<String, String> dianpaoMajiang;
    private static Map<String, String> fangpaoMajiang;

    public static Map<String, String> getWenzhouMajiang() {
        return wenzhouMajiang;
    }

    public static Map<String, String> getRuianMajiang() {
        return ruianMajiang;
    }

    public static Map<String, String> getDianpaoMajiang() {
        return dianpaoMajiang;
    }

    public static Map<String, String> getFangpaoMajiang() {
        return fangpaoMajiang;
    }

    static {
        wenzhouMajiang = new HashMap<>();
        ruianMajiang = new HashMap<>();
        dianpaoMajiang = new HashMap<>();
        fangpaoMajiang = new HashMap<>();

        //for 温州麻将
        ResourceBundle bundle = ResourceBundle.getBundle("wenzhouMajiangUrl");
        wenzhouMajiang.put("ready", bundle.getString("majiang.game.ready"));
        wenzhouMajiang.put("maidi", bundle.getString("majiang.mj.maidi"));
        wenzhouMajiang.put("panforme", bundle.getString("majiang.game.panforme"));
        wenzhouMajiang.put("action", bundle.getString("majiang.game.action"));
        wenzhouMajiang.put("finish", bundle.getString("majiang.game.finish"));
        wenzhouMajiang.put("readyNext", bundle.getString("majiang.game.readyNext"));
        wenzhouMajiang.put("vote", bundle.getString("majiang.game.vote"));
        wenzhouMajiang.put("queryVote", bundle.getString("majiang.game.queryvote"));
        wenzhouMajiang.put("leaveGame", bundle.getString("majiang.game.leavegame"));


        //for 瑞安麻将
        bundle = ResourceBundle.getBundle("ruianMajiangUrl");
        ruianMajiang.put("ready", bundle.getString("majiang.game.ready"));
        ruianMajiang.put("maidi", bundle.getString("majiang.mj.maidi"));
        ruianMajiang.put("panforme", bundle.getString("majiang.game.panforme"));
        ruianMajiang.put("action", bundle.getString("majiang.game.action"));
        ruianMajiang.put("finish", bundle.getString("majiang.game.finish"));
        ruianMajiang.put("readyNext", bundle.getString("majiang.game.readyNext"));
        ruianMajiang.put("vote", bundle.getString("majiang.game.vote"));
        ruianMajiang.put("queryVote", bundle.getString("majiang.game.queryvote"));
        ruianMajiang.put("leaveGame", bundle.getString("majiang.game.leavegame"));

        //for 瑞安麻将
        bundle = ResourceBundle.getBundle("dianpaoMajiangUrl");
        dianpaoMajiang.put("ready", bundle.getString("majiang.game.ready"));
        dianpaoMajiang.put("maidi", bundle.getString("majiang.mj.maidi"));
        dianpaoMajiang.put("panforme", bundle.getString("majiang.game.panforme"));
        dianpaoMajiang.put("action", bundle.getString("majiang.game.action"));
        dianpaoMajiang.put("finish", bundle.getString("majiang.game.finish"));
        dianpaoMajiang.put("readyNext", bundle.getString("majiang.game.readyNext"));
        dianpaoMajiang.put("vote", bundle.getString("majiang.game.vote"));
        dianpaoMajiang.put("queryVote", bundle.getString("majiang.game.queryvote"));
        dianpaoMajiang.put("leaveGame", bundle.getString("majiang.game.leavegame"));

        //for 瑞安麻将
        bundle = ResourceBundle.getBundle("fangpaoMajiangUrl");
        fangpaoMajiang.put("ready", bundle.getString("majiang.game.ready"));
        fangpaoMajiang.put("maidi", bundle.getString("majiang.mj.maidi"));
        fangpaoMajiang.put("panforme", bundle.getString("majiang.game.panforme"));
        fangpaoMajiang.put("action", bundle.getString("majiang.game.action"));
        fangpaoMajiang.put("finish", bundle.getString("majiang.game.finish"));
        fangpaoMajiang.put("readyNext", bundle.getString("majiang.game.readyNext"));
        fangpaoMajiang.put("vote", bundle.getString("majiang.game.vote"));
        fangpaoMajiang.put("queryVote", bundle.getString("majiang.game.queryvote"));
        fangpaoMajiang.put("leaveGame", bundle.getString("majiang.game.leavegame"));

    }
}
