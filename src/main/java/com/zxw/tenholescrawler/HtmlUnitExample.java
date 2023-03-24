package com.zxw.tenholescrawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.io.IOException;

public class HtmlUnitExample {
    // define usage of firefox, chrome or Edge
    private static final WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public static void main(String[] args) {
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getCookieManager().addCookie(new Cookie("www.tenholes.com", "ten_auth1", "Ruvrv2pR0WKkAYZkwqIrPGQwNTcxOTY3ZmEyZTVmOTBiNzBlODE1YmQxYTdhMmNjZTVkNDQ5ZjExMmE3MWZhY2JiZjNjMWVhMjIzNzg1N2HJQeZovwvdM1tZnEUtq9DhTXLO4woGv%2FuodyIYGjxOX4bQc7LGpWOFI9VWOlPERJxkrpdL9UBs97xJjeBGVwlI"));
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setTimeout(2000);
        webClient.getOptions().setUseInsecureSSL(true);
        // overcome problems in JavaScript
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());

        try {
            final HtmlPage page = webClient
                    .getPage("http://www.tenholes.com/lessons/view?id=65");
            DomNodeList<DomElement> doms = page.getElementsByTagName("video");
            System.out.println(page.getTextContent());
        } catch (FailingHttpStatusCodeException | IOException e) {
            e.printStackTrace();
        }
    }
}
