package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-crawling.properties")
public class NetflixService {

    private WebDriver WEB_DRIVER;

    private JavascriptExecutor JS_EXECUTOR;

    private WebDriverWait WAIT;

    @Value("${driver.chrome.driver_path}")
    private String WEB_DRIVER_PATH;

    @Value("${netflix.url}")
    private String NETFLIX_URL;

    @Value("${netflix.id}")
    private String NETFLIX_ID;

    @Value("${netflix.pwd}")
    private String NETFLIX_PWD;

    private final OTTContentsRepository ottContentsRepository;

    private final RankingInfoRepository rankingInfoRepository;

    private final String SERIES_CATEGORY = "오늘 대한민국의 TOP 10 시리즈";

    private final String MOVIE_CATEGORY = "오늘 대한민국의 TOP 10 영화";

    private final int[] pointMatrix = {25, 18, 15, 12, 10, 8, 6, 4, 2, 1};

    private final float[] rankingScoreMatrix = {10, 8};

    @Transactional
    public void crawlingMostWatchedNetflix() throws InterruptedException {
        init();

        WEB_DRIVER.get(NETFLIX_URL);

        login();

        Thread.sleep(5000);

        scroll();

        List<WebElement> mostWatchedElements;

        // 시리즈 Top 10, 영화 Top 10 두 가지 Element 크롤링
        mostWatchedElements = WEB_DRIVER.findElements(By.cssSelector("div[data-list-context='mostWatched']"));

        int categoryCount = 0;
        for (WebElement mostWatchedElement : mostWatchedElements) {
            JS_EXECUTOR.executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'center' });", mostWatchedElement);
            String category = mostWatchedElement.findElement(By.cssSelector(".row-header-title")).getText();
            log.info("[" + category + "]");

            float rakingScoreRating = rankingScoreMatrix[categoryCount++];

            List<OTTContents> mostWatchedMovies = new ArrayList<>();
            List<String> mostWatchedTitles = new ArrayList<>();     // 중복검사용 제목 저장

            // Top10 제목 크롤링
            int count = 0;  // Top10 크롤링을 위한 10개 카운팅 (순위와 같은 값)
            int i = 0;
            while (count < 10) {
                Thread.sleep(500);

                // 다음 요소가 존재하지 않을 시, 리스트를 다음으로 넘김
                if (mostWatchedElement.findElements(By.cssSelector("div.slider-item.slider-item-" + i)).isEmpty()) {
                    mostWatchedElement.findElement(By.cssSelector("span.handle.handleNext.active")).click();
                    i = 0;
                    continue;
                }

                WebElement element = mostWatchedElement.findElement(By.cssSelector("div.slider-item.slider-item-" + i++));
                String title = element.findElement(By.cssSelector("p.fallback-text")).getText();

                // 중복 추가 방지
                if (title.isEmpty() || mostWatchedTitles.contains(title)) {
                    continue;
                }

                OTTContents movie;
                if (ottContentsRepository.findAllByTitle(title).size() == 1) {
                    movie = ottContentsRepository.findOTTByTitle(title);
                } else if (ottContentsRepository.findAllByTitle(title).size() > 1) {
                    List<OTTContents> ottContentsList = ottContentsRepository.findAllByTitle(title);
                    movie = getLatestMovieByYear(ottContentsList);
                } else {
                    List<OTTContents> ottContentsList = ottContentsRepository.findAllBySubtitleListContainsIgnoreCase(title);
                    movie = getLatestMovieByYear(ottContentsList);
                }

                float rakingScore = pointMatrix[count] * rakingScoreRating;

                movie.updateRakingScore((int) rakingScore);

                ottContentsRepository.save(movie);

                mostWatchedMovies.add(movie);
                mostWatchedTitles.add(title);

                log.info(count + 1 + ": " + title);

                count++;
            }

            // 순위 정보 존재 시, RakingList만 업데이트
            if (rankingInfoRepository.existsRankingInfoByOttAndCategory("Netflix", category)) {
                RankingInfo existRankingInfo = rankingInfoRepository.findRankingInfoByOttAndCategory("Netflix", category);
                existRankingInfo.deleteRankingList();
                existRankingInfo.setRankingList(mostWatchedMovies);
                rankingInfoRepository.save(existRankingInfo);
            } else {
                // RankingInfo 객체 생성
                RankingInfo rankingInfo = RankingInfo.builder()
                        .ott("Netflix")
                        .category(category)
                        .rankingList(mostWatchedMovies)
                        .build();
                rankingInfoRepository.save(rankingInfo);
            }

            log.info("Crawling End: " + category);
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

        WAIT = new WebDriverWait(WEB_DRIVER, Duration.ofSeconds(10));

        log.info("Initialize");
    }

    public void login() throws InterruptedException {
        // 넷플릭스 로그인
        WEB_DRIVER.findElement(By.xpath("//*[@id=\":r0:\"]")).sendKeys(NETFLIX_ID);
        WEB_DRIVER.findElement(By.xpath("//*[@id=\":r3:\"]")).sendKeys(NETFLIX_PWD);
        WEB_DRIVER.findElement(By.xpath("//*[@id=\"appMountPoint\"]/div/div/div[2]/div/form/button")).submit();
        log.info("Login Success");

        Thread.sleep(3000);

        WebElement profileButton = WEB_DRIVER.findElement(By.xpath("//*[@id=\"appMountPoint\"]/div/div/div[1]/div[1]/div[2]/div/div/ul/li[1]/div/a"));  // li[n]을 통해 n번째 프로필 버튼 클릭

        WAIT.until(ExpectedConditions.elementToBeClickable(profileButton)).click();
        log.info("Click Profile Success");
    }

    public void scroll() throws InterruptedException {
        JS_EXECUTOR.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        for (int i = 0; i < 10; i++) {
            JS_EXECUTOR.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(1000);
        }
        log.info("Scroll Success");

        Thread.sleep(1000);

        // 모달창 제거용 클릭 이벤트 발생
        WebElement anyElement = WEB_DRIVER.findElement(By.cssSelector("body")); // 화면의 아무 부분이나 선택
        anyElement.click();

        Thread.sleep(1000);
    }

    public OTTContents getLatestMovieByYear(List<OTTContents> ottContentsList) {
        OTTContents movie = ottContentsList.get(0);
        for (OTTContents ottContents : ottContentsList) {
            if (ottContents.getOttList().contains("Netflix") && ottContents.getYear() > movie.getYear()) {
                movie = ottContents;
            }
        }
        return movie;
    }
}
