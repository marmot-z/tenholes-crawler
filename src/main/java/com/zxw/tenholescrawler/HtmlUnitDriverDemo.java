package com.zxw.tenholescrawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * class description
 *
 * @author zhangxunwei
 * @date 2023/3/24
 */
public class HtmlUnitDriverDemo {

    public static void main(String[] args) throws IOException, ParseException {
        WebDriver unitDriver = new HtmlUnitDriver(BrowserVersion.CHROME, true){
            @Override
            protected WebClient newWebClient(BrowserVersion version) {
                WebClient webClient = super.newWebClient(version);
                webClient.getOptions().setThrowExceptionOnScriptError(false);
                return webClient;
            }
        };

        unitDriver.get("http://www.tenholes.com/lessons/view?id=184");
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01");
        unitDriver.manage().addCookie(new Cookie("ten_auth1", "%2FnlEcCcJw3%2Fh3%2F0%2F6MqLgGI0YTVjMTEwMmQ5YzdjOGQ2Y2JkZDk1ZWIwMTdjYTBiNzIwZGYxMGFmYzYyZjg1MzJjMmE0Y2Y0MjQ3MjhiNTNx2yx9HHCGk9uiGardAHz99KQnY4mhRpBASnzRSu3bNT%2BMW%2BrYcSeQruvneFwHfwTH9hr1bqg4Sv5NHt5GYZ6%2B", "/", date));
        unitDriver.get("http://www.tenholes.com/lessons/view?id=184");
        System.out.println(unitDriver.getPageSource());

        unitDriver.quit();
    }
}
