package com.anbang.fake.thread;

import java.util.concurrent.ThreadFactory;

/**
 * 最底层
 *
 * @Author: 吴硕涵
 * @Date: 2019/1/14 11:01 AM
 * @Version 1.0
 */
public class PoolThreadFactory implements ThreadFactory {
    /**
     * 线程计数器
     */
    private int counter;
    /**
     * 线程名称
     */
    private String name;


    public PoolThreadFactory(String name) {
        counter = 0;
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable run) {
        Thread t = new Thread(run, name + "-Thread-" + counter);
        counter++;
        return t;
    }


}
