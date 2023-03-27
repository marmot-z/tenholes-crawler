package com.zxw.tenholescrawler;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.Function;

public class PagePreviewer {

    private WebDriver webDriver;

    public PagePreviewer(String indexPage, String authToken) {
        this.init(indexPage, authToken);
    }

    private void init(String indexPage, String authToken) {
        // 初始化 webDriver
        // 设置系统变量，用于后续找到 driver 位置
        final String absoluteDriverPath = PagePreviewer.class
                .getResource("/chromedriver_mac_arm64/chromedriver").getPath();
        System.setProperty("webdriver.chrome.driver", absoluteDriverPath);

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(
                "--headless",
                "--ignore-certificate-errors",
                "--silent",
                "--window-position=-32000,-32000",
                "--allowed-ips=*",
                "--remote-allow-origins=*"
        );

        this.webDriver = new ChromeDriver(chromeOptions);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DATE, 7);
        Date cookieExpireDay = calendar.getTime();

        // 访问首页，添加身份 cookie
        this.webDriver.get(indexPage);
        this.webDriver.manage().addCookie(new Cookie("ten_auth1", authToken, "/", cookieExpireDay));
    }

    public String getPagePreview(String pageUrl, Function<WebDriver, Boolean> determineCompletedFn) {
        this.webDriver.get(pageUrl);

        // 等待若干秒等待页面加载
        new WebDriverWait(webDriver, Duration.ofSeconds(3))
                .until(driver -> determineCompletedFn.apply(driver));

        return webDriver.getPageSource();
    }

    public void close() {
        this.webDriver.quit();
    }
}
