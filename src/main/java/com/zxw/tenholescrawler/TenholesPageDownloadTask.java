package com.zxw.tenholescrawler;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TenholesPageDownloadTask {

    private String htmlUrl;
    private File outputDir;

    public TenholesPageDownloadTask(String url, String outputDir) {
        this.htmlUrl = url;
        this.outputDir = new File(outputDir);

        if (!this.outputDir.exists()) {
            throw new IllegalStateException(outputDir + "文件夹不存在");
        }
    }

    public void download() throws IOException {
        File htmlFile = Downloader.download(htmlUrl, outputDir.getAbsolutePath() + "/index.html");
        Document doc = Jsoup.parse(htmlFile);

        String htmlContent = doc.html();
        Pattern pattern = Pattern.compile("(http://authcdn.tenholes.com/videoAll.*?)\"");
        Matcher matcher = pattern.matcher(htmlContent);
        while (matcher.find()) {
            String src = matcher.group(1);

            if (StringUtils.isBlank(src)) {
                continue;
            }

            String filename = resolveFileName(src);
            if (!src.startsWith("http")) {
                src = "http://www.tenholes.com" + src;
            }

            Downloader.download(src, outputDir.getAbsolutePath() + "/" + filename);
            // 写回原先元素的地址
            // video.attr("src", "./" + filename);
        }

        // 下载文件到本地，并且替换原页面中的地址
        for (Element audio : doc.select("audio")) {
            String src = audio.attr("src");

            if (StringUtils.isBlank(src)) {
                continue;
            }

            String filename = resolveFileName(src);

            if (!src.startsWith("http")) {
                src = "http://www.tenholes.com" + src;
            }

            Downloader.download(src, outputDir.getAbsolutePath() + "/" + filename);
            audio.attr("src", "./" + filename);
        }

        // TODO 下载 js、css 以及图片文件

        save2file(htmlFile, doc);
    }

    private static String resolveFileName(String src) {
        int dotIndex = src.lastIndexOf("/"), questionIndex;
        questionIndex = (questionIndex = src.lastIndexOf("?")) == -1 ?
                src.length() :
                questionIndex;

        return src.substring(dotIndex + 1, questionIndex);
    }

    private void save2file(File file, Document doc) throws IOException {
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(doc.outerHtml());
        }
    }

    public static void main(String[] args) throws IOException {
        String url = "http://www.tenholes.com/lessons/view?id=65";
        String outputDir = "/Users/zxw/Desktop/test1";

        new TenholesPageDownloadTask(url, outputDir).download();
    }
}
