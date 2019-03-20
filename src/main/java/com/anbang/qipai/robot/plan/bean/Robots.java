package com.anbang.qipai.robot.plan.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:25 PM
 * @Version 1.0
 */
public class Robots {
    static public Map<String,RobotMemberDbo> availableRobot;
    static public Map<String,RobotMemberDbo> taskedRobot;

    static {
        availableRobot = new HashMap<>();
        taskedRobot = new HashMap<>();
    }

    public static Map<String, RobotMemberDbo> getAvailableRobot() {
        return availableRobot;
    }

    public static void setAvailableRobot(Map<String, RobotMemberDbo> availableRobot) {
        Robots.availableRobot = availableRobot;
    }

    public static Map<String, RobotMemberDbo> getTaskedRobot() {
        return taskedRobot;
    }

    public static void setTaskedRobot(Map<String, RobotMemberDbo> taskedRobot) {
        Robots.taskedRobot = taskedRobot;
    }
}
