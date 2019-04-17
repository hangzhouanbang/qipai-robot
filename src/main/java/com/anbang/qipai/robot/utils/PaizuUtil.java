package com.anbang.qipai.robot.utils;

import com.dml.doudizhu.player.action.da.solution.DaPaiDianShuSolution;
import com.dml.puke.pai.DianShu;

import java.util.Arrays;
import java.util.List;

public class PaizuUtil {
    public static boolean isBoom(DaPaiDianShuSolution shuSolution) {
        List<DianShu> dianShuList = Arrays.asList(shuSolution.getDachuDianShuArray());
        if (dianShuList.size() == 4) {
            if (dianShuList.get(0).equals(dianShuList.get(1)) && dianShuList.get(0).equals(dianShuList.get(2))
                    && dianShuList.get(0).equals(dianShuList.get(3))) {
                return true;
            }
        }

        if (dianShuList.size() == 2) {
            if (dianShuList.contains(DianShu.dawang) || dianShuList.contains(DianShu.xiaowang)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containBoom(DaPaiDianShuSolution shuSolution) {
        List<DianShu> dianShuList = Arrays.asList(shuSolution.getDachuDianShuArray());
        if (dianShuList.size() > 4) {
            if (dianShuList.get(0).equals(dianShuList.get(1)) && dianShuList.get(0).equals(dianShuList.get(2))
                    && dianShuList.get(0).equals(dianShuList.get(3))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBoomOrWang(DaPaiDianShuSolution shuSolution) {
        List<DianShu> dianShuList = Arrays.asList(shuSolution.getDachuDianShuArray());
        if (dianShuList.size() == 4) {
            if (dianShuList.get(0).equals(dianShuList.get(1)) && dianShuList.get(0).equals(dianShuList.get(2))
                    && dianShuList.get(0).equals(dianShuList.get(3))) {
                return true;
            }
        }

        if (dianShuList.size() == 2 || dianShuList.size() == 1) {
            if (dianShuList.contains(DianShu.dawang) || dianShuList.contains(DianShu.xiaowang)) {
                return true;
            }
        }
        return false;
    }
}
