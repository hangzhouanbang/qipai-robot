package com.anbang.fake.model;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/22 2:15 PM
 * @Version 1.0
 */
public class MajiangPlayerAction {
    String id;
    String type;

    LinkedList<String> paiList = new LinkedList<>();

    //仅仅供打使用
    public void add(String daId,String pai){
        //TODO:ID为将要打出的牌的ID  缺一个算法
        if(StringUtils.isEmpty(id)){
            id = daId;
            type="da";
            paiList.add(pai);
        }
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
