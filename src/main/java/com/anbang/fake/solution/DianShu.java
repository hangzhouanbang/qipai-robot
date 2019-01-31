package com.anbang.fake.solution;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/21 3:57 PM
 * @Version 1.0
 */
public enum DianShu {
    san, si, wu, liu, qi, ba, jiu, shi, J, Q, K, A, er, xiaowang, dawang;

    public static DianShu getDianShuByOrdinal(int ordinal) {
        DianShu[] dianshuZu = DianShu.values();
        return dianshuZu[ordinal];
    }
}
