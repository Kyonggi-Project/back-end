package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.domain.RankingInfo;
import org.project.simproject.repository.mongoRepo.OTTContentsRepository;
import org.project.simproject.repository.mongoRepo.RankingInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-crawling.properties")
public class CoupangPlayService {
    private final RankingInfoRepository rankingInfoRepository;
    private final OTTContentsRepository ottRepository;

    private WebDriver WEB_DRIVER;
    private JavascriptExecutor JS_EXECUTOR;
    private WebDriverWait WAIT;

    @Value("${driver.chrome.driver_path}")
    private String WEB_DRIVER_PATH;

    @Value("${coupang_play.url}")
    private String COUPANG_PLAY_URL;

    @Value("${coupang_id}")
    private String COUPANG_PLAY_ID;

    @Value("${coupang_pass}")
    private String COUPANG_PLAY_PWD;

    private String Popular = "'이번 주 인기작 TOP 20'";

    private String Movie = "'이번 주 인기 영화 Top 20'";

    public RankingInfo save(RankingInfo rankingInfo) {
        return RankingInfo
                .builder()
                .ott(rankingInfo.getOtt())
                .category(rankingInfo.getCategory())
                .rankingList(rankingInfo.getRankingList())
                .build();
    }
    public void crawlingMostWatchedCoupangPlay() throws InterruptedException {
        init();

        WEB_DRIVER.get(COUPANG_PLAY_URL);

        login();

        Thread.sleep(5000);

        scroll();

        Thread.sleep(2000);

        RankingInfo Top20Series = seriesRanking(Popular);

        Thread.sleep(1000);

        RankingInfo Top20Movies = movieRanking(Movie);

//        for (RankingInfo result : rankingInfoRepository.findAll()) {
//            System.out.println(result.getCategory());
//            List<OTTContents> ottContents = result.getRankingList();
//            System.out.println(result.getRankingList().size());
//            for(OTTContents ott : ottContents) {
//                if(ott == null) {
//                    continue;
//                }
//                System.out.println(ott.getTitle());
//            }
//        }

        WEB_DRIVER.quit();
    }
    @Transactional
    public RankingInfo seriesRanking(String CrawlingStr) throws InterruptedException {
        RankingInfo seriesRankingInfo = getRankingInfo(CrawlingStr);
        if (rankingInfoRepository.existsRankingInfoByOttAndCategory(seriesRankingInfo.getOtt(), seriesRankingInfo.getCategory())) { // 유무 검사 내가 boolean 값으로 만들어뒀는데 이제 푸시할 거
            RankingInfo Info = rankingInfoRepository.findRankingInfoByOttAndCategory(seriesRankingInfo.getOtt(), seriesRankingInfo.getCategory());      // 해당 RankingInfo 객체 찾기
            rankingInfoRepository.delete(Info);
        }
        return rankingInfoRepository.save(seriesRankingInfo);
    }
    @Transactional
    public RankingInfo movieRanking(String CrawlingStr) throws InterruptedException {
        RankingInfo movieRankingInfo = getRankingInfo(CrawlingStr);
        if (rankingInfoRepository.existsRankingInfoByOttAndCategory(movieRankingInfo.getOtt(), movieRankingInfo.getCategory())) { // 유무 검사 내가 boolean 값으로 만들어뒀는데 이제 푸시할 거
            RankingInfo Info = rankingInfoRepository.findRankingInfoByOttAndCategory(movieRankingInfo.getOtt(), movieRankingInfo.getCategory());      // 해당 RankingInfo 객체 찾기
            rankingInfoRepository.delete(Info);
        }
        return rankingInfoRepository.save(movieRankingInfo);
    }
    public RankingInfo getRankingInfo(String CrawlingStr) throws InterruptedException {
        List<String> titleList = getList(CrawlingStr);
        WebElement mostWatchedElement = WEB_DRIVER.findElement(By.xpath("//*[contains(text(),"+CrawlingStr+")]/.."));
        List<OTTContents> ottContents = new ArrayList<>();
        for(String ottTitle:titleList) {
            if(ottRepository.existsOTTByTitle(ottTitle)) {
                ottContents.add(ottRepository.findOTTByTitle(ottTitle));
            }
        }
        RankingInfo rankingInfo = new RankingInfo("CoupangPlay",CrawlingStr,ottContents);
        log.info("Contents>>>>>>>>"+ottContents);
        return rankingInfo;
    }
    public List<String> getList(String CrawlingStr) throws InterruptedException {
        List<String> titleList = new ArrayList<>();
        String title = "";
        WebElement mostWatchedElement = WEB_DRIVER.findElement(By.xpath("//*[contains(text(),"+CrawlingStr+")]/.."));

        String category = mostWatchedElement.findElement(By.cssSelector("h1")).getText();
        JS_EXECUTOR.executeScript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center', inline: 'center'});", mostWatchedElement); // mostWatchedElement를 화면 가운데로 이동

        log.info(category);
        WebElement ContentList = mostWatchedElement.findElement(By.xpath("./div[1]/div[1]"));

        for (int j = 1; j <= 20; j++) {
            WebElement Content = ContentList.findElement(By.xpath("./div[" + j + "]"));
            Thread.sleep(1000);
            try {
                if(j > 5 && j % 5 == 1) {
                    throw new Exception();
                }
                WebDriverWait wait = new WebDriverWait(WEB_DRIVER, Duration.ofSeconds(15));
                if (wait.until(ExpectedConditions.visibilityOf(Content)) != null) {
                    wait.until(ExpectedConditions.elementToBeClickable(Content));
                    title = crawlingTitleName(category,Content,mostWatchedElement, j);
                }
            } catch (Exception e) {
                WebElement right = ContentList.findElement(By.xpath("//*[@id='right']"));
                right.click();
                Thread.sleep(3000);
                WebDriverWait wait = new WebDriverWait(WEB_DRIVER, Duration.ofSeconds(20));
                if (wait.until(ExpectedConditions.visibilityOf(Content)) != null) {
                    // 요소가 화면에 보임
                    wait.until(ExpectedConditions.elementToBeClickable(Content));
                    title = crawlingTitleName(category,Content,mostWatchedElement, j);
                    Thread.sleep(5000);
                }
            }
            titleList.add(title);
        }
        log.info("titleList>>>>>"+titleList);

        return titleList;
    }
    public String crawlingTitleName(String category, WebElement Content, WebElement mostWatchedElement, int j) throws InterruptedException {
        String title ="";
        log.info("[" + category + "] Visit Success: " + j);
        WebDriverWait wait = new WebDriverWait(WEB_DRIVER, Duration.ofSeconds(20));
        if(wait.until(ExpectedConditions.visibilityOf(Content)) != null) {
            wait.until(ExpectedConditions.elementToBeClickable(Content));
            Thread.sleep(3000);

            Actions actions = new Actions(WEB_DRIVER);
            actions.moveToElement(Content).perform();
            Thread.sleep(3000);

            JavascriptExecutor js = (JavascriptExecutor) WEB_DRIVER;
            Object hoveredElement = js.executeScript("return document.querySelector(':hover')");

            if (hoveredElement != null) {
                WebElement tmpElement = mostWatchedElement.findElement(By.xpath("./.."));
                title = tmpElement.findElement(By.cssSelector("#previewModalWrapper")).getText().split("\\r?\\n")[0];
                WEB_DRIVER.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                actions.moveToElement(WEB_DRIVER.findElement(By.tagName("body"))).perform();
            }
        }
        return title;
    }

    public void init() {
        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");

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

        Thread.sleep(5000);

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