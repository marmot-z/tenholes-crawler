package com.zxw.tenholescrawler;

import com.beust.ah.A;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PageDownloadTask {

    private PagePreviewer previewer;
    private File outputDir;

    public PageDownloadTask(PagePreviewer previewer, File outputDir) {
        this.previewer = previewer;
        this.outputDir = outputDir;
    }

    private static final String INDEX_PAGE = "http://www.tenholes.com";

    public void download(String pageUrl) {
        // 下载完整页面，转换成 dom
        String html = previewer.getPagePreview(pageUrl);
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
            CompletableFuture<Void> completableFuture = asyncDownload(src, outputFile,
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
        CompletableFuture<Void> completableFuture = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]))
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

    private CompletableFuture<Void> asyncDownload(String url, File outputFile, Consumer<String> doneCallback) {
        final CompletableFuture<Void> result = new CompletableFuture<>();
        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5))
                .build();
        final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setIOReactorConfig(ioReactorConfig)
                .build();

        client.start();

        final SimpleHttpRequest request = SimpleRequestBuilder.get()
                .setUri(url)
                .build();
        client.execute(
                SimpleRequestProducer.create(request),
                SimpleResponseConsumer.create(),
                new FutureCallback<>() {
                    @Override
                    public void completed(final SimpleHttpResponse response) {
                        if (new StatusLine(response).getStatusCode() != 200) {
                            System.err.println("下载 " + url + " 文件失败");
                        }

                        try {
                            FileOutputStream outputStream = new FileOutputStream(outputFile);
                            outputStream.write(response.getBodyBytes());

                            doneCallback.accept(outputFile.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        result.complete(null);
                    }

                    @Override
                    public void failed(final Exception ex) {
                        result.completeExceptionally(ex);
                    }

                    @Override
                    public void cancelled() {
                        result.complete(null);
                    }

                });

        return result;
    }

    private void saveHtml(File file, Document doc) {
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(doc.outerHtml());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String authToken = "xxx";
        PagePreviewer pagePreviewer = new PagePreviewer(INDEX_PAGE, authToken);
        PageDownloadTask task = new PageDownloadTask(pagePreviewer, new File("/Users/zxw/Desktop/test1"));

        task.download("http://www.tenholes.com/lessons/view?id=180");

        pagePreviewer.close();
    }
}
