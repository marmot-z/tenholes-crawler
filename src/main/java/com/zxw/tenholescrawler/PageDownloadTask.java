package com.zxw.tenholescrawler;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.zxw.tenholescrawler.ListPageDownloadTask.TASK_THREAD_LOCAL;

public class PageDownloadTask {

    /**
     * 下载页面 url
     */
    private String pageUrl;
    /**
     * 页面 previewer，用于加载完整的页面
     */
    private PagePreviewer previewer;
    /**
     * 页面下载目录
     */
    private File outputDir;

    public PageDownloadTask(String pageUrl, PagePreviewer previewer, File outputDir) {
        this.pageUrl = pageUrl;
        this.previewer = previewer;
        this.outputDir = outputDir;
    }

    public static final String INDEX_PAGE = "http://www.tenholes.com";

    public void download() {
        System.out.println("> [" + TASK_THREAD_LOCAL.get() + "] 开始下载：" + pageUrl);

        String html = previewer.getPagePreview(pageUrl, driver -> {
            try {
                WebElement video = driver.findElement(By.tagName("video"));
                String src = video.getDomAttribute("src");
                // video 的链接存在则说明页面加载完毕
                return StringUtils.isNotBlank(src);
            } catch (NoSuchElementException e) {
                // 无 video 元素则页面直接返回
                return true;
            }
        });
        Document doc = Jsoup.parse(html);

        // 下载页面媒体资源
        Elements videos = doc.select("video");
        Elements audios = doc.select("audio");
        List<Element> medias = ListUtils.union(videos, audios);
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>(medias.size());

        for (Element media : medias) {
            String src = media.attr("src");
            String filename = resolveFileName(src);

            if (!src.startsWith("http")) {
                src = INDEX_PAGE + src;
            }

            File outputFile = new File(outputDir, filename);
            CompletableFuture<Void> completableFuture = Downloader.asyncDownload(src, outputFile,
                    (unused) -> media.attr("src", "./" + filename));

            completableFutures.add(completableFuture);
        }

        // 替换页面元素（js、css、img）链接地址
        replaceTagElementSrc(doc,
                Pair.of("script", "src"),
                Pair.of("link", "href"),
                Pair.of("image", "src")
        );

        // 将修改后的文件保存到本地
        CompletableFuture<Void> completableFuture = CompletableFuture.allOf(
                completableFutures.toArray(new CompletableFuture[0]))
                .whenComplete((unused, e) -> {
                    if (Objects.nonNull(e)) {
                        e.printStackTrace();
                    } else {
                        saveHtml(new File(outputDir, "index.html"), doc);
                    }
                });

        completableFuture.join();
    }

    private void replaceTagElementSrc(Document doc, Pair<String, String>... tags) {
        for (Pair<String, String> pair : tags) {
            Elements els = doc.getElementsByTag(pair.getLeft());
            String srcAttributeName = pair.getRight();

            for (Element el : els) {
                String src = el.attr(srcAttributeName);

                if (StringUtils.isNotBlank(src) && !src.startsWith("http")) {
                    el.attr(srcAttributeName, INDEX_PAGE + src);
                }
            }
        }
    }

    private static String resolveFileName(String src) {
        int dotIndex = src.lastIndexOf("/"), questionIndex;
        questionIndex = (questionIndex = src.lastIndexOf("?")) == -1 ?
                src.length() :
                questionIndex;

        return src.substring(dotIndex + 1, questionIndex);
    }

    private void saveHtml(File file, Document doc) {
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(doc.outerHtml());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
