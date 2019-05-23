package com.anbang.qipai.robot.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @Description: 斗地主、跑的快 url config
 */
public class PukeUrlConfig {
    private static Map<String, String> doudizhu;
    private static Map<String, String> paodekuai;

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

        paodekuai = new HashMap<>();
        ResourceBundle bundle1 = ResourceBundle.getBundle("paodekuaiUrl");
        paodekuai.put("ready",bundle1.getString("paodekuai.game.ready"));
        paodekuai.put("finish",bundle1.getString("paodekuai.game.finish"));
        paodekuai.put("vote",bundle1.getString("paodekuai.game.vote"));
        paodekuai.put("queryvote",bundle1.getString("paodekuai.game.queryvote"));
        paodekuai.put("leavegame",bundle1.getString("paodekuai.game.leavegame"));
        paodekuai.put("panforme",bundle1.getString("paodekuai.pk.panforme"));
        paodekuai.put("da",bundle1.getString("paodekuai.pk.da"));
        paodekuai.put("guo",bundle1.getString("paodekuai.pk.guo"));
        paodekuai.put("readyNext",bundle1.getString("paodekuai.pk.readyNext"));
    }

    public static Map<String, String> getDoudizhu() {
        return doudizhu;
    }

    public static Map<String, String> getPaodekuai() {
        return paodekuai;
    }
}
