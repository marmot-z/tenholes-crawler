package com.zxw.tenholescrawler;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.*;

public class Downloader {
    public static File download(String url, String outputFile) throws IOException {
        File file = new File(outputFile);
        HttpGet get = new HttpGet(url);

        get.setHeader(new BasicHeader("Cookie", "ten_auth1=gPxgDhNWJgXbnPLlPNWFPDNlZWNmZDc5YmZlMjE5YzdiNmM4MzQwMTkxM2Q2MDU4ZTAwMzM1ZDc3MDRmNjA5ODVhYTZiNDIyZTUyYjY4YjXeCZe3ZXTNpIYDmfXSEKVeMJloMIVo6UxAaYVT1lOuoo6Z4CmkvLyTbyE0HGNgK03mwRctCF8T%2B%2BdtV61HXrPJ;"));

        CloseableHttpResponse response = HttpClients.createDefault().execute(get);

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new RuntimeException("下载 " + url + " 资源失败");
        }

        try (InputStream inputStream = response.getEntity().getContent();
             OutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(inputStream, outputStream);
        }

        return file;
    }
}
