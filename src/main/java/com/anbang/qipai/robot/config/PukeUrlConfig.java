package com.anbang.qipai.robot.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @Description: 斗地主 url config
 */
public class PukeUrlConfig {
    private static Map<String, String> doudizhu;

    static {
        doudizhu = new HashMap<>();

        ResourceBundle bundle = ResourceBundle.getBundle("doudizhuUrl");
        doudizhu.put("ready",bundle.getString("doudizhu.game.ready"));
        doudizhu.put("finish",bundle.getString("doudizhu.game.finish"));
        doudizhu.put("vote",bundle.getString("doudizhu.game.vote"));
        doudizhu.put("queryvote",bundle.getString("doudizhu.game.queryvote"));
        doudizhu.put("leavegame",bundle.getString("doudizhu.game.leavegame"));
        doudizhu.put("panforme",bundle.getString("doudizhu.pk.panforme"));
        doudizhu.put("da",bundle.getString("doudizhu.pk.da"));
        doudizhu.put("guo",bundle.getString("doudizhu.pk.guo"));
        doudizhu.put("readyNext",bundle.getString("doudizhu.pk.readyNext"));
        doudizhu.put("qiangdizhu",bundle.getString("doudizhu.pk.qiangdizhu"));
    }

    public static Map<String, String> getDoudizhu() {
        return doudizhu;
    }
}
