package com.zxw.tenholescrawler;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.SimpleTimeZone;

public class HeadlessBrowserDemo {
    public static void main(String[] args) throws IOException, ParseException {
        String chromeDriverPath = "/Users/zhangxunwei/idea-workspace/tenholes-crawler/src/main/resources/chromedriver_mac_arm64/chromedriver";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        // 创建了一个 Firefox driver 的实例
        // 注意，其余的代码依赖于接口而非实例
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless","--ignore-certificate-errors", "--silent");
        options.addArguments("--window-position=-32000,-32000");
        options.addArguments("--allowed-ips=*");
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01");
//        driver.manage().addCookie(new Cookie("_csrf-frontend", "ac9afb3957cd8ec94bd2fbb98d7ae221dcaa9656e4b0c9f0e7f00096f9736f84a%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22DOhKO2058KLQoYi3Axaa7HB6dIh-PsPw%22%3B%7D", "/", date));
//        driver.manage().addCookie(new Cookie("Hm_lvt_2f7f7866ed2b0addd933476e1018bb2a", "1677939911,1679576706,1679581979", "/", date));
//        driver.manage().addCookie(new Cookie("Hm_lpvt_2f7f7866ed2b0addd933476e1018bb2a", "1679583596", "/", date));
//        driver.manage().addCookie(new Cookie("4661870num", "1", "/", date));

        // 使用它访问 Google
        driver.get("http://www.tenholes.com/lessons/view?id=184");
        driver.manage().addCookie(new Cookie("ten_auth1", "%2FnlEcCcJw3%2Fh3%2F0%2F6MqLgGI0YTVjMTEwMmQ5YzdjOGQ2Y2JkZDk1ZWIwMTdjYTBiNzIwZGYxMGFmYzYyZjg1MzJjMmE0Y2Y0MjQ3MjhiNTNx2yx9HHCGk9uiGardAHz99KQnY4mhRpBASnzRSu3bNT%2BMW%2BrYcSeQruvneFwHfwTH9hr1bqg4Sv5NHt5GYZ6%2B", "/", date));
        driver.get("http://www.tenholes.com/lessons/view?id=184");

        // Google 搜索结果由 JavaScript 动态渲染
        // 等待页面加载完毕，超时时间设为10秒
        (new WebDriverWait(driver, Duration.ofSeconds(4))).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
//                return d.getTitle().toLowerCase().startsWith("cheese!");
                return true;
            }
        });

        System.out.println(driver.getPageSource());

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File("/Users/zhangxunwei/Desktop/screenshot.png"));

        //关闭浏览器
        driver.quit();
    }
}
