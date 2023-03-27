package com.zxw.tenholescrawler;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.Objects;

import static com.zxw.tenholescrawler.PageDownloadTask.INDEX_PAGE;

public class ListPageDownloadTask {

    /**
     * webDriver 不是线程安全的，所以为每个线程创建私有的 previewer<br>
     * previewer 虽然被多个对象共享，但实际运行过程中是串行运行的，所以不存在线程安全问题
     */
    private PagePreviewer pagePreviewer;
    /**
     * 页面元素迭代器
     */
    private ListPageIterator iterator;
    /**
     * 文件输出目录
     */
    private File outputDir;

    public ListPageDownloadTask(String listPageUrl, String authToken, File outputDir) {
        this.pagePreviewer = new PagePreviewer(INDEX_PAGE, authToken);
        this.iterator = new ListPageIterator(listPageUrl, element -> {
            Element span = element.selectFirst("a > span");
            String className = span.className();

            return Objects.equals(className, "vip1");
        }, pagePreviewer);
        this.outputDir = outputDir;
    }

    public void download() {
        while (iterator.hasNext()) {
            Element anchor = iterator.next().selectFirst("a");
            String href = anchor.attr("href");

            if (!href.startsWith("http")) {
                href = INDEX_PAGE + href;
            }

            File pageOutputDir = new File(outputDir, StringUtils.trim(anchor.text()));
            pageOutputDir.mkdirs();

            PageDownloadTask pageDownloadTask = new PageDownloadTask(pagePreviewer, pageOutputDir);
            try {
                pageDownloadTask.download(href);
            } catch (Exception e) {
                System.err.println("下载 " + href + " 页面失败");
                e.printStackTrace();
            }
        }

        pagePreviewer.close();
    }
}
