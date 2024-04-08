package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.project.simproject.domain.Movie;
import org.project.simproject.repository.mongoRepo.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-crawling.properties")
public class KinolightsCrawlingService {

    private WebDriver WEB_DRIVER;

    private JavascriptExecutor JS_EXECUTOR;

    private WebDriverWait WAIT;

    @Value("${driver.chrome.driver_path}")
    private String WEB_DRIVER_PATH;

    private final String KINOLIGHTS_URL = "https://m.kinolights.com/discover/explore";

    List<String> OTT_LIST = new ArrayList<>();

    private final MovieRepository movieRepository;

    public void crawlingMovies() throws InterruptedException {
        init();

        int j = 2;
        for (String ott : OTT_LIST) {
            WEB_DRIVER.get(KINOLIGHTS_URL);
            WebElement buttonElement = WEB_DRIVER.findElement(By.xpath("//*[@id=\"contents\"]/section/div[2]/div/div/div/div[" + j++ + "]/button"));
            Thread.sleep(5000);
            JS_EXECUTOR.executeScript("arguments[0].click();", buttonElement);

            log.info(ott + " Crawling Start");

            scroll();

            List<String> hrefList = collectHref();

            int count = 0;

            for (String hrefLink : hrefList) {
                WEB_DRIVER.get(hrefLink);
                try {
                    crawlingInfo(count++, ott);
                } catch (Exception e) {
                    log.error("Error: Crawling " + ott + " - " +  count);
                    log.error("ErrorMessage: " + e.getMessage());
                }
            }
        }

        // 웹 드라이버 종료
        WEB_DRIVER.quit();
    }

    public void init() {
        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
        chromeOptions.addArguments("headless");                     // 브라우저 안띄우게 설정
        chromeOptions.addArguments("--disable-gpu");                // gpu 비활성화(headless 적용하기 위해 필요)

        WEB_DRIVER = new ChromeDriver(chromeOptions);
        JS_EXECUTOR = (JavascriptExecutor) WEB_DRIVER;

        WAIT = new WebDriverWait(WEB_DRIVER, Duration.ofSeconds(3));

        OTT_LIST.add("Netflix");
        OTT_LIST.add("Tving");
        OTT_LIST.add("Coupang Play");
        OTT_LIST.add("Wavve");
    }

    public void scroll() {
        JS_EXECUTOR.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        WebDriverWait scrollWait = new WebDriverWait(WEB_DRIVER, Duration.ofSeconds(1)); // 최대 30초까지 대기

        // 요소 발견 여부 확인
        boolean found = false;

        // 반복해서 스크롤하여 요소 찾기
        while (!found) {
            try {
                // WebDriverWait를 사용하여 요소를 찾을 때까지 대기
                scrollWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.maximumLoad__wrap")));
                found = true; // 요소를 찾았으므로 found 변수를 true로 설정하여 반복문 종료
            } catch (TimeoutException e) {
                // 요소가 발견되지 않은 경우 스크롤을 추가로 내리기
                JS_EXECUTOR.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            }
        }
        log.info("Scroll Success");
    }

    public List<String> collectHref() {
        List<WebElement> movieList = WEB_DRIVER.findElements(By.cssSelector("div.MovieItem.grid"));
//        log.info("Get MovieList Success");
//        log.info("Start get Info");

        List<String> hrefList = new ArrayList<>();

        for (WebElement movie : movieList) {
            hrefList.add(movie.findElement(By.tagName("a")).getAttribute("href"));
        }

        return hrefList;
    }

    public void crawlingInfo(int count, String ott) {
        WebElement titleElement = WAIT.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.movie-title-wrap h2.title-kr")));
        String title = titleElement.getText();

        if (movieRepository.existsByTitle(title)) {
            Movie updateMovie = movieRepository.findByTitle(title);
            if (updateMovie.getOttList().contains(ott)) {
                return;
            } else {
                updateMovie.addOtt(ott);
                movieRepository.save(updateMovie);
                return;
            }
        }

        String posterImgUrl = WEB_DRIVER.findElement(By.cssSelector("div.poster img.movie-poster")).getAttribute("src");
        String backgroundImgUrl = WEB_DRIVER.findElement(By.cssSelector("div.movie-image-area img")).getAttribute("src");

        WebElement metadataElement = WEB_DRIVER.findElement(By.cssSelector("p.metadata"));
        int year = Integer.parseInt(metadataElement.findElement(By.cssSelector("span.metadata-item:last-child")).getText());
//        log.info(count + ": [" + title + "] (" + year + ")");

        // 작품 특징
        List<WebElement> tagElementList = WEB_DRIVER.findElements(By.cssSelector("ul.metadata li.metadata__item"));
        List<String> tagList = new ArrayList<>();
        for (WebElement tagElement : tagElementList) {
            String seriesGenre = tagElement.getText();
            tagList.add(seriesGenre);
//            log.info(seriesGenre);
        }

        // 줄거리
        String synopsis = "";
        if (!WEB_DRIVER.findElements(By.cssSelector("div.synopsis__text-wrap div.text span")).isEmpty()) {
//            WebElement synopsisElement = WEB_DRIVER.findElement(By.cssSelector("div.synopsis__text-wrap div.text span"));
            WebElement synopsisElement = WEB_DRIVER.findElement(By.cssSelector("div.synopsis__text-wrap"));
            if (!synopsisElement.findElements(By.tagName("button")).isEmpty()) {
                WebElement buttonElement = synopsisElement.findElement(By.tagName("button"));
                JS_EXECUTOR.executeScript("arguments[0].click();", buttonElement);
            }
            synopsis = synopsisElement.getText();
//            log.info(synopsis);
        }

        // 배우 정보 저장
        Map<String, String> actorCharacterMap = new HashMap<>();
        if (!WEB_DRIVER.findElements(By.id("actorList")).isEmpty()) {
//            log.info("Start Crawling Actors");
            WebElement actorList = WEB_DRIVER.findElement(By.id("actorList"));
            List<WebElement> castElements = actorList.findElements(By.cssSelector("div.person.list__avatar"));

            if (!castElements.isEmpty()) {
                for (WebElement castElement : castElements) {
                    String name = castElement.findElement(By.cssSelector("div.name")).getText();
                    WebElement characterElement;
                    String character = "";

                    try {
                        // character 요소를 찾음
                        characterElement = castElement.findElement(By.cssSelector("div.character"));
                        // character 요소의 텍스트를 가져옴
                        character = characterElement.getText();
                    } catch (NoSuchElementException e) {
                        // character 요소가 존재하지 않는 경우, character 값을 빈 문자열로 유지
                    }
                    if (name.contains(".")) {
                        name = name.replace(".", "");
                    }

                    actorCharacterMap.put(name, character);
                }
            }
        }

        // 제작진 정보 저장
        Map<String, String> staffMap = new HashMap<>();
        if (!WEB_DRIVER.findElements(By.id("staffList")).isEmpty()) {
//            log.info("Start Crawling Staffs");
            WebElement staffList = WEB_DRIVER.findElement(By.id("staffList"));
            List<WebElement> staffElements = staffList.findElements(By.cssSelector("div.person.list__avatar"));

            for (WebElement staffElement : staffElements) {
                String name = staffElement.findElement(By.cssSelector("div.name")).getText();
                WebElement characterElement;
                String position = "";

                try {
                    // character 요소를 찾음
                    characterElement = staffElement.findElement(By.cssSelector("div.character"));
                    // character 요소의 텍스트를 가져옴
                    position = characterElement.getText();
                } catch (NoSuchElementException e) {
                    // character 요소가 존재하지 않는 경우, character 값을 빈 문자열로 유지
                }

                if (name.contains(".")) {
                    name = name.replace(".", "");
                }
                staffMap.put(name, position);
            }
        }

        // Movie 객체 생성 및 데이터 설정
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setYear(year);
        movie.setSynopsis(synopsis);
        movie.setPosterImg(posterImgUrl);
        movie.setBackgroundImg(backgroundImgUrl);
        movie.setTagList(tagList);
        movie.setActorList(actorCharacterMap);
        movie.setStaffList(staffMap);
        movie.addOtt(ott);

        movie.setScore(0);
        movie.setReviewCount(0);
        movie.setRating(0);

        // MongoDB에 저장
        movieRepository.save(movie);

        log.info("[" + ott + "] " + count + ": [" + title + "] (" + year + ")");
    }
}