package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.project.simproject.repository.mongoRepo.MovieRepository;
import org.project.simproject.repository.mongoRepo.RankingInfoRepository;
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
public class CoupangPlayService {

    private WebDriver WEB_DRIVER;

    private JavascriptExecutor JS_EXECUTOR;

    private WebDriverWait WAIT;

    @Value("${driver.chrome.driver_path}")
    private String WEB_DRIVER_PATH;

    @Value("${coupang_play.url}")
    private String COUPANG_PLAY_URL;

    @Value("${coupang_play.id}")
    private String COUPANG_PLAY_ID;

    @Value("${coupang_play.pwd}")
    private String COUPANG_PLAY_PWD;

    private final MovieRepository movieRepository;

    private final RankingInfoRepository rankingInfoRepository;

    public void crawlingMostWatchedCoupangPlay() throws InterruptedException {
        init();

//        LocalDate crawlingDate = LocalDate.now();

        WEB_DRIVER.get(COUPANG_PLAY_URL);

        login();

        Thread.sleep(5000);

        scroll();

        Actions actions = new Actions(WEB_DRIVER);

//        String Top20Series = "이번 주 인기작 TOP 20";
//        String Top20Movies = "이번 주 인기 영화 TOP 20";

        List<WebElement> mostWatchedElements = WEB_DRIVER.findElements(By.xpath("//*/text()[contains(., 'Top 20')]/ancestor::div"));

        for (int i = 0; i < mostWatchedElements.size(); i++) {
            WebElement mostWatchedElement = mostWatchedElements.get(i);
            String category = mostWatchedElement.findElement(By.cssSelector("h1.top-titles")).getText();
            log.info(category);
            JS_EXECUTOR.executeScript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center', inline: 'center'});", mostWatchedElement); // mostWatchedElement를 화면 가운데로 이동

            List<String> titleList = new ArrayList<>();
            String div = "/div";
            for (int j = 1; j <= 20; j++) {
                WebElement movieElement = mostWatchedElement.findElement(By.xpath("." + div.repeat(4 - i) + "[" + j + "]"));
//                WebElement movieElement = mostWatchedElement.findElement(By.xpath("./div/div/div[" + j + "]"));
                log.info("[" + category + "] Visit Success: " + j);
                Thread.sleep(1000);
                actions.moveToElement(movieElement).perform();
                Thread.sleep(1000);
                String title = WEB_DRIVER.findElement(By.cssSelector("#previewModalWrapper")).getText().split("\\r?\\n")[0];

/*                if (title.isEmpty() || titleList.contains(title)) {
                    log.info("Retry: " + title);
                    j--;
                    continue;
                }

                titleList.add(title);*/
                log.info(title);
                Thread.sleep(1000);
                actions.moveToElement(WEB_DRIVER.findElement(By.xpath("//*[@id=\"__next\"]/div[1]/a/img"))).perform();
            }
//            RankingInfo rankingInfo = new RankingInfo();
//            rankingInfo.setOtt("Netflix");
//            rankingInfo.setCategory(category);
//            rankingInfo.setRankingList(mostWatchedMovies);
//                rankingInfo.setDate(crawlingDate);

//            rankingInfoRepository.save(rankingInfo);
        }

        WEB_DRIVER.quit();
    }

    public void init() {
        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
//        chromeOptions.addArguments("headless");                     // 브라우저 안띄우게 설정
//        chromeOptions.addArguments("--disable-gpu");                // gpu 비활성화(headless 적용하기 위해 필요)

        WEB_DRIVER = new ChromeDriver(chromeOptions);
        JS_EXECUTOR = (JavascriptExecutor) WEB_DRIVER;

        WAIT = new WebDriverWait(WEB_DRIVER, Duration.ofSeconds(3));

        log.info("Initialize");
    }

    public void login() throws InterruptedException {
        // 쿠팡플레이 로그인
        WEB_DRIVER.findElement(By.xpath("//*[@id=\"__next\"]/div[1]/section[1]/article/div/a[2]")).click();

        Thread.sleep(1000);
        WEB_DRIVER.findElement(By.cssSelector("#login-email-input\n")).sendKeys(COUPANG_PLAY_ID);
        WEB_DRIVER.findElement(By.cssSelector("#login-password-input")).sendKeys(COUPANG_PLAY_PWD);
        WEB_DRIVER.findElement(By.xpath("/html/body/div[1]/div/div/form/button")).submit();
        log.info("Login Success");

        Thread.sleep(3000);

        WebElement profileButton = WEB_DRIVER.findElement(By.xpath("//*[@id=\"__next\"]/div[1]/div/div[2]/div[1]/div"));
        WAIT.until(ExpectedConditions.elementToBeClickable(profileButton)).click();
        log.info("Click Profile Success");
    }

    public void scroll() throws InterruptedException {
        JS_EXECUTOR.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        WebDriverWait scrollWait = new WebDriverWait(WEB_DRIVER, Duration.ofSeconds(1)); // 최대 30초까지 대기

        for (int i = 0; i < 10; i++) {
            JS_EXECUTOR.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(1000);
        }
        log.info("Scroll Success");
    }

}
