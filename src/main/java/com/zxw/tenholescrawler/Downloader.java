package com.zxw.tenholescrawler;

import org.apache.hc.client5.http.async.methods.AbstractBinResponseConsumer;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Downloader {

    public static CompletableFuture<Void> asyncDownload(String url, File outputFile, Consumer<String> doneCallback) {
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
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outputFile);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
        final CompletableFuture<Void> result = new CompletableFuture<>();

        client.execute(
                new BasicRequestProducer(request, null),
                new AbstractBinResponseConsumer<Void>() {
                    @Override
                    protected void start(final HttpResponse response,
                                         final ContentType contentType) throws HttpException, IOException {
                        int statusCode = new StatusLine(response).getStatusCode();

                        if (statusCode != HttpStatus.SC_OK) {
                            result.completeExceptionally(
                                    new IllegalStateException("资源 " + url + " 下载响应码为 " + statusCode));
                        }
                    }

                    @Override
                    protected int capacityIncrement() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    protected void data(ByteBuffer byteBuffer, boolean endOfStream) throws IOException {
                        try {
                            if (byteBuffer != null) {
                                if (byteBuffer.hasArray()) {
                                    outputStream.write(byteBuffer.array(),
                                            byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
                                } else {
                                    while (byteBuffer.hasRemaining()) {
                                        outputStream.write(byteBuffer.get());
                                    }
                                }
                            }
                        } catch (IOException e) {
                            result.completeExceptionally(e);
                        }

                        if (endOfStream) {
                            outputStream.close();
                            result.complete(null);
                            doneCallback.accept(outputFile.getName());
                        }
                    }

                    @Override
                    protected Void buildResult() {return null;}

                    @Override
                    public void failed(final Exception cause) {
                        result.completeExceptionally(cause);
                    }

                    @Override
                    public void releaseResources() {}
                }, null);

        return result;
    }
}
