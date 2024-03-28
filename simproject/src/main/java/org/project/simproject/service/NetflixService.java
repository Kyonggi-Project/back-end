package org.project.simproject.service;

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
import org.project.simproject.domain.Movie;
import org.project.simproject.domain.Ranking;
import org.project.simproject.domain.RankingInfo;
import org.project.simproject.repository.MovieRepository;
import org.project.simproject.repository.NetflixRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
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

    private final NetflixRepository netflixRepository;

    private final MovieRepository movieRepository;

    public void crawlingMostWatchedNetflix() throws InterruptedException {
        init();

        LocalDate crawlingDate = LocalDate.now();

        WEB_DRIVER.get(NETFLIX_URL);

        login();

        Thread.sleep(5000);

        scroll();

        // 오늘의 대한민국 TOP10과 영화 TOP10 두 가지 카테고리 크롤링
        List<WebElement> mostWatchedElements = WEB_DRIVER.findElements(By.cssSelector("div[data-list-context='mostWatched']"));
        log.info(mostWatchedElements.toString());
//        WebElement mostWatchedElement = WEB_DRIVER.findElement(By.cssSelector("div[data-list-context='mostWatched']"));

        for (WebElement mostWatchedElement : mostWatchedElements) {
            String category = mostWatchedElement.findElement(By.cssSelector(".row-header-title")).getText();
            log.info(category);

//            List<WebElement> mostWatchedList = new ArrayList<>();
            List<Ranking> mostWatchedMovies = new ArrayList<>();

            // Top10 제목 크롤링
            int count = 1;  // Top10 크롤링을 위한 10개 카운팅
            int i = 0;
            while (count <= 10) {
                if (mostWatchedElement.findElements(By.cssSelector("div.slider-item.slider-item-" + i)).isEmpty()) {
                    mostWatchedElement.findElement(By.xpath("//span[@class='handle handleNext active']")).click();
                    i = 0;
                    continue;
                }
                WebElement element = mostWatchedElement.findElement(By.cssSelector("div.slider-item.slider-item-" + i++));
                String title = element.findElement(By.cssSelector("p.fallback-text")).getText();

                // 중복 추가 방지
                if (title.isEmpty() || mostWatchedMovies.stream().anyMatch(r -> r.getMovie().getTitle().equals(title))) {
                    continue;
                }

                log.info(count + ": " + title);
                int rank = count++;
                Movie movie = movieRepository.findByTitle(title); // 영화 제목으로 MongoDB에서 해당 영화 정보를 가져옴
                if (movie != null) {
                    Ranking rankingMovie = Ranking.builder()
                            .movie(movie)
                            .rank(rank)
                            .build();
                    mostWatchedMovies.add(rankingMovie);
                }


                RankingInfo rankingInfo = new RankingInfo();
                rankingInfo.setOtt("Netflix");
                rankingInfo.setCategory(category);
                rankingInfo.setRankings(mostWatchedMovies);
                rankingInfo.setDate(crawlingDate);

            }

            WEB_DRIVER.quit();
        }
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
        // 넷플릭스 로그인
        WEB_DRIVER.findElement(By.xpath("//*[@id=\":r0:\"]")).sendKeys(NETFLIX_ID);
        WEB_DRIVER.findElement(By.xpath("//*[@id=\":r3:\"]")).sendKeys(NETFLIX_PWD);
        WEB_DRIVER.findElement(By.xpath("//*[@id=\"appMountPoint\"]/div/div/div[2]/div/form/button")).submit();
        log.info("Login Success");

        Thread.sleep(3000);

        WebElement profileButton = WEB_DRIVER.findElement(By.xpath("//*[@id=\"appMountPoint\"]/div/div/div[1]/div[1]/div[2]/div/div/ul/li[3]/div/a"));

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

    public void getInfoByModal() {
        // Modal 창에서 정보를 알려주는 div 선택
        // Modal창을 선택하는 방법 추가 탐색 필요
        WebElement modalElement = WEB_DRIVER.findElement(By.cssSelector("div.previewModal--detailsMetadata.detail-modal.has-smaller-buttons[data-uia='previewModal--detailsMetadata']"));
        String year = modalElement.findElement(By.cssSelector("div.year")).getText();
        List<String> actors = new ArrayList<>();            // 출연
        List<String> genres = new ArrayList<>();            // 장르
        List<String> seriesGenres = new ArrayList<>();      // 시리즈 특징

        // 출연자 이름 저장
        WebElement actorsElement = modalElement.findElement(By.cssSelector("div.previewModal--tags[data-uia='previewModal-tags-person']"));
        for (WebElement actorElement : actorsElement.findElements(By.cssSelector("span.tag-item"))) {
            String actor = actorElement.getText();
            if (actor.equals("더 보기") || actor.isEmpty())
                break;
            actors.add(actor.replaceAll(",", ""));
        }

        // 장르 저장
        WebElement genresElement = modalElement.findElement(By.cssSelector("div.previewModal--tags[data-uia='previewModal--tags-genre']"));
        for (WebElement genreElement : genresElement.findElements(By.cssSelector("span.tag-item"))) {
            String genre = genreElement.getText();
            if (genre.equals("더 보기") || genre.isEmpty())
                break;
            genres.add(genre.replaceAll(",", ""));
        }

        // 시리즈 특징 저장
        WebElement seriesGenresElement = modalElement.findElement(By.cssSelector("div.previewModal--tags[data-uia='previewModal-tags-genre']"));
        for (WebElement seriesGenreElement : seriesGenresElement.findElements(By.cssSelector("span.tag-item"))) {
            String seriesGenre = seriesGenreElement.getText();
            if (seriesGenre.equals("더 보기") || seriesGenre.isEmpty())
                break;
            seriesGenres.add(seriesGenre.replaceAll(",", ""));
        }


    }

}
