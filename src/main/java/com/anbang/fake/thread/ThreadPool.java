package com.anbang.fake.thread;

import java.util.concurrent.*;

/**
 * 中间层
 *
 * @Author: 吴硕涵
 * @Date: 2019/1/14 11:03 AM
 * @Version 1.0
 */
public class ThreadPool {
    /**
     * 创建ThreadPoolExecutor线程池对象，并初始化该对象的各种参数
     */
    private ThreadPoolExecutor threadPoolExecutor;

    public ThreadPool(String threadFactoryName) {
        //设置核心池大小
        int corePoolSize = 30;
        //设置线程池最大能接受多少线程
        int maximumPoolSize = 30;
        //当前线程数大于corePoolSize、小于maximumPoolSize时，超出corePoolSize的线程数的生命周期
        long keepActiveTime = 0L;
        //设置时间单位，毫秒
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        //设置线程池缓存队列的排队策略
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
        //线程工厂
        ThreadFactory threadFactory = new PoolThreadFactory(threadFactoryName);
        //创建线程池
        this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepActiveTime, timeUnit, workQueue, threadFactory);
    }

    public void execute(final Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    public boolean isShutdown() {
        return threadPoolExecutor.isShutdown();
    }

    public void shutdown() {
        threadPoolExecutor.shutdown();
    }

}
