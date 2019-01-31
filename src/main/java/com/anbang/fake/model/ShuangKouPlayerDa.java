package com.anbang.fake.model;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/22 9:23 AM
 * @Version 1.0
 */
public class ShuangKouPlayerDa {
    String memberId;
    String position;
    JSONObject json;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
