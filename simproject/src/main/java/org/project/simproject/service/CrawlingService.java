package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-crawling.properties")
public class CrawlingService {
    private WebDriver webDriver;
    @Value("${driver.chrome.driver_path}")
    private String WEB_DRIVER_PATH;

    public void startCrawling() throws IOException, InterruptedException {

        String url = "https://www.netflix.com/kr/browse/genre/839338";

        log.info("OTT 크롤링");

        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
        chromeOptions.addArguments("headless");                     // 브라우저 안띄우게 설정
        chromeOptions.addArguments("--disable-gpu");                // gpu 비활성화(headless 적용하기 위해 필요)

        webDriver = new ChromeDriver(chromeOptions);

        webDriver.get(url);

        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        System.out.println(webDriver.getTitle());

        webDriver.quit();
    }
}
