package com.anbang.qipai.robot.plan.bean;

import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:24 PM
 * @Version 1.0
 */
public class MajiangPlayerAction {
    String id;
    String type;

    LinkedList<String> paiList = new LinkedList<>();

    //仅仅供打使用
    public void add(String daId, String pai) {
        //TODO:ID为将要打出的牌的ID  缺一个算法
        id = daId;
        type = "da";
        paiList.add(pai);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
