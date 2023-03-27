package com.zxw.tenholescrawler;

import java.util.concurrent.*;

public class ThreadPools {
    public final static ExecutorService CRAWLER_THREAD_POOL =
            new ThreadPoolExecutor(20,
            40,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(200));
}
