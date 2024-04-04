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

//        String Top20Series = "이번 주 인기작 TOP 20";
//        String Top20Movies = "이번 주 인기 영화 TOP 20";

        List<WebElement> mostWatchedElements = WEB_DRIVER.findElements(By.xpath("//*/text()[contains(., 'Top 20')]/ancestor::div"));

        for (WebElement mostWatchedElement : mostWatchedElements) {
            String category = mostWatchedElement.findElement(By.cssSelector("h1.top-titles")).getText();
            log.info(category);

            List<WebElement> movieElements = mostWatchedElement.findElements(By.cssSelector("div[data-cy='carouselThumbnail']"));
            log.info("Get Movie List Success" + movieElements.size());

//            List<Movie> mostWatchedMovies = new ArrayList<>();
            List<String> mostWatchedMovies = new ArrayList<>();

            Thread.sleep(1000);

            int count = 1;  // Top20 크롤링을 위한 20개 카운팅
            int i = 0;
            while (count <= 20) {
                WebElement movieElement = movieElements.get(i++);
                try {
                    Actions actions = new Actions(WEB_DRIVER);
                    actions.moveToElement(movieElement).perform();
/*                    String title = WEB_DRIVER.findElement(By.cssSelector("div.PreviewModalHeader_previewModalTextHeader__Z90IE")).getText();
                    if (mostWatchedMovies.contains(title)) {
                        continue;
                    }
                    mostWatchedMovies.add(title);
                    log.info(title);*/
                } catch (Exception e) {
                    mostWatchedElement.findElement(By.cssSelector("div#right")).click();
                    log.info("Success Click Right Button");
                }
            }

/*
            while (count <= 20) {
                // 다음 요소가 존재하지 않을 시, 리스트를 다음으로 넘김
                if (mostWatchedElement.findElements(By.cssSelector("div.slider-item.slider-item-" + i)).isEmpty()) {
                    mostWatchedElement.findElement(By.cssSelector("span.handle.handleNext.active")).click();
                    i = 0;
                    continue;
                }

                WebElement element = mostWatchedElement.findElement(By.cssSelector("div.slider-item.slider-item-" + i++));
                String title = element.findElement(By.cssSelector("p.fallback-text")).getText();
//                Movie movie = movieRepository.findByTitle(title);
                String movie = title;

                // 중복 추가 방지
                if (title.isEmpty() || mostWatchedMovies.contains(movie)) {
                    continue;
                }

                mostWatchedMovies.add(movie);

                log.info(count + ": " + title);

                count++;
            }
*/

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
