package com.anbang.qipai.robot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:14 PM
 * @Version 1.0
 */

//温州双扣
public class UrlConfig {
    private static Logger logger = LoggerFactory.getLogger(UrlConfig.class);
    private static String findRoomUrl;
    private static String testUrl;
    private static String tryTokenUrl;
    private static String wechatLoginUrl;
    private static String joinRAMJRoomUrl;
    private static String joinFPMJRoomUrl;
    private static String joinWZMJRoomUrl;
    private static String joinDPMJRoomUrl;
    private static String joinWZSKRoomUrl;
    private static String readyUrl;
    private static String panformeUrl;
    private static String guoUrl;
    private static String daUrl;
    private static String shuangkouReadyNext;
    private static String shuangkouVote;
    private static String gameInfoUrl;
    private static String shuangkouQueryVoteUrl;
    private static String hallIndexUrl;
    private static String jiesanUrl;
    private static String leaveGame;

    private static String infoRAMJUrl;
    private static String infoDPMJUrl;
    private static String infoFPMJUrl;
    private static String infoWZMJUrl;
    private static String infoWZSKUrl;
    private static String infoDDZUrl;

    private static String joinXiuXianChang;
    private static String giveGoldUrl;

    public UrlConfig() {
    }

    public static String getTestUrl() {
        return testUrl;
    }


    public static String getTryTokenUrl() {
        return tryTokenUrl;
    }


    public static String getWechatLoginUrl() {
        return wechatLoginUrl;
    }


    public static String getJoinRAMJRoomUrl() {
        return joinRAMJRoomUrl;
    }

    public static String getJoinFPMJRoomUrl() {
        return joinFPMJRoomUrl;
    }

    public static String getJoinWZMJRoomUrl() {
        return joinWZMJRoomUrl;
    }

    public static String getJoinDPMJRoomUrl() {
        return joinDPMJRoomUrl;
    }

    public static String getJoinWZSKRoomUrl() {
        return joinWZSKRoomUrl;
    }

    public static String getFindRoomUrl() {
        return findRoomUrl;
    }

    public static String getReadyUrl() {
        return readyUrl;
    }

    public static String getPanformeUrl() {
        return panformeUrl;
    }

    public static String getGuoUrl() {
        return guoUrl;
    }

    public static String getDaUrl() {
        return daUrl;
    }

    public static String getInfoRAMJUrl() {
        return infoRAMJUrl;
    }

    public static String getInfoDPMJUrl() {
        return infoDPMJUrl;
    }

    public static String getInfoFPMJUrl() {
        return infoFPMJUrl;
    }

    public static String getInfoWZMJUrl() {
        return infoWZMJUrl;
    }

    public static String getInfoWZSKUrl() {
        return infoWZSKUrl;
    }

    public static String getInfoDDZUrl() {
        return infoDDZUrl;
    }

    public static String getShuangkouReadyNext() {
        return shuangkouReadyNext;
    }

    public static String getShuangkouVote() {
        return shuangkouVote;
    }

    public static String getGameInfoUrl() {
        return gameInfoUrl;
    }

    public static String getShuangkouQueryVoteUrl() {
        return shuangkouQueryVoteUrl;
    }

    public static String getHallIndexUrl() {
        return hallIndexUrl;
    }

    public static String getLeaveGame() {
        return leaveGame;
    }

    public static String getJiesanUrl() {
        return jiesanUrl;
    }

    public static String getJoinXiuXianChang() {
        return joinXiuXianChang;
    }

    public static String getGiveGoldUrl() {
        return giveGoldUrl;
    }

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("url");
        findRoomUrl = bundle.getString("games.hall.findroom");
        testUrl = bundle.getString("members.test.save");
        tryTokenUrl = bundle.getString("members.auth.trytoken");
        wechatLoginUrl = bundle.getString("members.thirdauth.wechatidlogin");
        joinDPMJRoomUrl = bundle.getString("games.xiuxian.joinDPMJRoom");
        joinFPMJRoomUrl = bundle.getString("games.xiuxian.joinFPMJRoom");
        joinRAMJRoomUrl = bundle.getString("games.xiuxian.joinRAMJRoom");
        joinWZMJRoomUrl = bundle.getString("games.xiuxian.joinWZMJRoom");
        joinWZSKRoomUrl = bundle.getString("games.xiuxian.joinWZSKRoom");


        readyUrl = bundle.getString("shuangkou.game.ready");
        panformeUrl = bundle.getString("shuangkou.game.panforme");
        guoUrl = bundle.getString("shuangkou.game.guo");
        daUrl = bundle.getString("shuangkou.game.da");
        shuangkouReadyNext = bundle.getString("shuangkou.game.readyNext");
        shuangkouVote = bundle.getString("shuangkou.game.vote");
        gameInfoUrl = bundle.getString("shuangkou.game.info");
        shuangkouQueryVoteUrl = bundle.getString("shuangkou.game.queryvote");
        hallIndexUrl = bundle.getString("games.hall.index");
        jiesanUrl = bundle.getString("shuangkou.game.finish");
        leaveGame = bundle.getString("shuangkou.game.leavegame");

        infoDPMJUrl = bundle.getString("games.xiuxian.infoDPMJRoom");
        infoFPMJUrl = bundle.getString("games.xiuxian.infoFPMJRoom");
        infoWZMJUrl = bundle.getString("games.xiuxian.infoWZMJRoom");
        infoRAMJUrl = bundle.getString("games.xiuxian.infoRAMJRoom");
        infoWZSKUrl = bundle.getString("games.xiuxian.infoWZSKRoom");
        infoDDZUrl = bundle.getString("games.xiuxian.infoDDZRoom");

        joinXiuXianChang = bundle.getString("xiuxianchang.game.joinroom");
        giveGoldUrl = bundle.getString("xiuxianchang.member.givegold");
    }
}
