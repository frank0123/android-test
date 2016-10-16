package com.example.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtil {
    //constructor
    private ThreadPoolUtil(){}

    //the number of core thread
    private static int CORE_THREAD_NUM = 5;
    //the max-number of the threads in pool
    private static int MAX_THREAD_NUM = 100;
    //10 seconds of the extra thread while in hanging status
    private static int KEEP_ALIVE_TIME = 10000;
    //block thread(set off the extra-thread when the core thread is blocked and the thread queue is full)
    private static BlockingQueue<Runnable> blockingDeque = new ArrayBlockingQueue<Runnable>(10);
    //executor pool of the thread
    public static ThreadPoolExecutor threadPoolExecutor;

    //thread factory
    public static ThreadFactory threadFactory = new ThreadFactory() {
        //auto-count calculator of the thread's number
        private final AtomicInteger atomicInteger = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "the current thread numbers in the pool：" + atomicInteger.getAndIncrement());
        }
    };

    static {
        threadPoolExecutor = new ThreadPoolExecutor(CORE_THREAD_NUM, MAX_THREAD_NUM, KEEP_ALIVE_TIME, TimeUnit.SECONDS, blockingDeque, threadFactory);
    }

    //extract the thread from the pool and implement the specified runnable object
    public static void execute(Runnable runnable){
        threadPoolExecutor.execute(runnable);
    }
}
