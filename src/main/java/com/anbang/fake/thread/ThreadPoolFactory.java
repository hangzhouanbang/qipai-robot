package com.anbang.fake.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * 最高层
 *
 * @Author: 吴硕涵
 * @Date: 2019/1/14 11:04 AM
 * @Version 1.0
 */
public class ThreadPoolFactory {
    public static Map<String,ThreadPool> poolMap=new HashMap<>();

    public static synchronized ThreadPool getThreadPool(String poolName){
        if (!poolMap.containsKey(poolName)){
            poolMap.put(poolName,new ThreadPool(poolName));
        }
        return poolMap.get(poolName);
    }
}
