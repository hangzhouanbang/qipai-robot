package com.anbang.qipai.robot.observe;

import java.util.HashMap;

/**
 * @Author: 吴硕涵
 * @Date: 2019/3/4 11:05 AM
 * @Version 1.0
 */
public interface Observer {
    //当主题状态改变时,会将一个String类型字符传入该方法的参数,每个观察者都需要实现该方法
    public void tuoguan();
}
