package com.zxw.tenholescrawler;

import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        // 输出目录
        String outputDir = args[0];
        // 身份验证 token
        String authToken = args[1];
        List<String> crawlingListPageUrls = Arrays.asList(
                "http://www.tenholes.com/lessons/list?cid=3",
                "http://www.tenholes.com/lessons/list?cid=15",
                "http://www.tenholes.com/lessons/list?cid=8",
                "http://www.tenholes.com/lessons/list?cid=10",
                "http://www.tenholes.com/lessons/list?cid=13",
                "http://www.tenholes.com/lessons/search?action=new&page=1"
        );

        System.out.println("============ 开始下载页面 ============");

        StopWatch stopWatch = StopWatch.createStarted();
        List<CompletableFuture<Void>> downloadFutures = new ArrayList<>(crawlingListPageUrls.size());
        for (String listPageUrl : crawlingListPageUrls) {
            CompletableFuture<Void> downloadFuture = CompletableFuture.supplyAsync(() -> {
                ListPageDownloadTask task = new ListPageDownloadTask(listPageUrl, authToken, new File(outputDir));
                task.download();
                return null;
            }, ThreadPools.CRAWLER_THREAD_POOL);

            downloadFutures.add(downloadFuture);
        }

        // 等待所有任务完成
        for (CompletableFuture<Void> downloadFuture : downloadFutures) {
            downloadFuture.join();
        }

        System.out.println("============ 结束下载页面，耗时: " + stopWatch.getTime() + "ms ============");

        // 线程池销毁
        try {
            ThreadPools.CRAWLER_THREAD_POOL.shutdown();
            ThreadPools.CRAWLER_THREAD_POOL.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("线程池销毁等待超时（10s），退出程序");
            e.printStackTrace();
        }
    }
}
