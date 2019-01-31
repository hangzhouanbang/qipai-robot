package com.anbang.fake.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @Author: 吴硕涵
 * @Date: 2019/1/18 5:11 PM
 * @Version 1.0
 */
public class ConditionPool {
    public static Map<String, Condition> conditionMap = new HashMap<>();

    public static Map<String, ReentrantLock> reentrantLockMap = new HashMap<>();

    public static void addCondition(String conditionId, Condition condition) {
        conditionMap.put(conditionId, condition);
    }

    public static void removeCondition(String conditionId) {
        conditionMap.remove(conditionId);
    }

    public static void addLock(String conditionId, ReentrantLock lock) {
        reentrantLockMap.put(conditionId, lock);
    }

    public static void removeLock(String conditionId) {
        reentrantLockMap.remove(conditionId);
    }

}
