package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-crawling.properties")
public class WavveService {

    private WebDriver WEB_DRIVER;

    private JavascriptExecutor JS_EXECUTOR;

    private WebDriverWait WAIT;

    @Value("${driver.chrome.driver_path}")
    private String WEB_DRIVER_PATH;

    @Value("${wavve.url}")
    private String WAVEE_URL;

    public void crawlingMostWatchedWavve() throws InterruptedException {
        init();

        WEB_DRIVER.get(WAVEE_URL);

//        login();

        Thread.sleep(5000);

//        WebElement mostWatchedElement = WEB_DRIVER.findElement(By.cssSelector("div.multi.portrait-cell.ranking-band"));
//        WebElement mostWatchedElement = WEB_DRIVER.findElement(By.cssSelector("div.multi.portrait-cell.ranking-band[id='multisection_index_9']"));
        List<WebElement> mostWatchedElement = WEB_DRIVER.findElements(By.cssSelector("div.multi.portrait-cell.ranking-band"));
        log.info(mostWatchedElement.toString());

        log.info("Find Ranking-band Success: " + mostWatchedElement);
//        String category = mostWatchedElement.findElement(By.cssSelector("div.title > h1.title-area > span.label")).getText();
        String category = mostWatchedElement.get(0).findElement(By.cssSelector("div.title")).getText();
        log.info(category);

        List<WebElement> mostWatchedList = mostWatchedElement.get(0).findElements(By.cssSelector("div.swiper-slide"));
        List<String> mostWatchedNames = new ArrayList<>();

        // Top20 제목 크롤링
        int count = 1;  // Top20 크롤링을 위한 20개 카운팅
        int i = 0;

        for (WebElement mostWatched : mostWatchedList) {
            String title = mostWatched.findElement(By.cssSelector("span.alt-text")).getText();
            log.info(count++ + ": " + title);
        }

//        while (count <= 20) {
//            if (mostWatchedElement.findElements(By.cssSelector("div.slider-item.slider-item-" + i)).isEmpty()) {
//                mostWatchedElement.findElement(By.xpath("//span[@class='handle handleNext active']")).click();
//                i = 0;
//                continue;
//            }
//            WebElement element = mostWatchedElement.findElement(By.cssSelector("div.slider-item.slider-item-" + i++));
//            String title = element.findElement(By.cssSelector("div.thumb.portrait > a > div.thumb-image > div.no-thumb > span.alt-text")).getText();
//            if (mostWatchedNames.contains(title) || title.isEmpty())
//                continue;
//            mostWatchedNames.add(title);
//            log.info(count++ + ": " + title);
//
//            /*
//            String imageUrl = element.findElement(By.cssSelector("img.boxart-image-in-padded-container")).getText();
//
//            Netflix netflix = Netflix.builder()
//                    .title(title)
//                    .category(category)
//                    .ranking((long) count++)
//                    .build();
//
//            netflixRepository.save(netflix);*/
//        }

        log.info(mostWatchedNames.toString());

        WEB_DRIVER.quit();
    }

    public void init() {
        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
//        chromeOptions.addArguments("headless");                     // 브라우저 안띄우게 설정
//        chromeOptions.addArguments("--disable-gpu");                // gpu 비활성화(headless 적용하기 위해 필요)

        WEB_DRIVER = new ChromeDriver(chromeOptions);
        JS_EXECUTOR = (JavascriptExecutor) WEB_DRIVER;

        WAIT = new WebDriverWait(WEB_DRIVER, Duration.ofSeconds(3));

        log.info("Initialize");
    }

/*    public void login() throws InterruptedException {
        // 넷플릭스 로그인
        WEB_DRIVER.findElement(By.xpath("//*[@id=\"appMountPoint\"]/div/div/div[2]/div/form/button")).submit();
        log.info("Login Success");

        Thread.sleep(3000);

        WebElement profileButton = WEB_DRIVER.findElement(By.xpath("//*[@id=\"appMountPoint\"]/div/div/div[1]/div[1]/div[2]/div/div/ul/li[3]/div/a"));

        WAIT.until(ExpectedConditions.elementToBeClickable(profileButton)).click();
        log.info("Click Profile Success");
    }*/

}
