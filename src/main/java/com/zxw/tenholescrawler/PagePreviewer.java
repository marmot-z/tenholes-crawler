package com.zxw.tenholescrawler;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PagePreviewer {

    private WebDriver webDriver;

    public PagePreviewer(String indexPage, String authToken) {
        this.init(indexPage, authToken);
    }

    private void init(String indexPage, String authToken) {
        // 初始化 webDriver
        // 设置系统变量，用于后续找到 driver 位置
//        final String driverPath = "./resources/chromedriver_mac_arm64/chromedriver";
        final String driverPath = "/Users/zxw/ideaworkspace/tenholes-crawler/src/main/resources/chromedriver_mac_arm64/chromedriver";
        final String absoluteDriverPath = new File(driverPath).getAbsolutePath();
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

    public String getPagePreview(String pageUrl) {
        this.webDriver.get(pageUrl);

        // 等待 3 秒等待页面加载
        new WebDriverWait(webDriver, Duration.ofSeconds(3))
                .until(driver -> {
                    WebElement video = driver.findElement(By.tagName("video"));
                    String src = video.getDomAttribute("src");

                    // video 的链接存在则说明页面加载完毕
                    return StringUtils.isNotBlank(src);
                });

        return webDriver.getPageSource();
    }

    public void close() {
        this.webDriver.quit();
    }
}
