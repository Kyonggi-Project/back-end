package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
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
import java.util.*;

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
                crawlingInfo(count++, ott);
/*                try {
                    crawlingInfo(count++, ott);
                } catch (Exception e) {
                    log.error("Error: Crawling " + ott + " - " + count);
                    log.error("ErrorMessage: " + e.getMessage());
                }*/
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

        WebElement metadataYearElement = WEB_DRIVER.findElement(By.cssSelector("p.metadata"));
        int year = Integer.parseInt(metadataYearElement.findElement(By.cssSelector("span.metadata-item:last-child")).getText());
//        log.info(count + ": [" + title + "] (" + year + ")");

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

        // 메타데이터
        Map<String, String> metadataMap = new HashMap<>();
        List<String> genreList = new ArrayList<>();
        WebElement metadata = WEB_DRIVER.findElement(By.cssSelector("ul.metadata"));
        List<WebElement> metadataElements = metadata.findElements(By.cssSelector("li.metadata__item"));
        for (WebElement metadataElement : metadataElements) {
            if (!metadataElement.isDisplayed()) {
                continue; // 화면에 표시되지 않으면 건너뜁니다.
            }
            String itemTitle = metadataElement.findElement(By.cssSelector("span.item__title")).getText();
            String itemBody = metadataElement.findElement(By.cssSelector("span.item__body")).getText();
            if (itemTitle.equals("장르")) {
                String[] genres  = itemBody.split(",\\s*");
                genreList.addAll(Arrays.asList(genres));
                continue;
            }
            metadataMap.put(itemTitle, itemBody);
        }

        // 배우 정보 저장
        Map<String, String> actorCharacterMap = new HashMap<>();
        try {
            WebElement actorList = WEB_DRIVER.findElement(By.cssSelector("div.person__actor"));
            if (!actorList.findElements(By.cssSelector("div.person.list__avatar")).isEmpty()) {
//            log.info("Start Crawling Actors");
                List<WebElement> castElements = actorList.findElements(By.cssSelector("div.person.list__avatar"));

                if (!castElements.isEmpty()) {
                    for (WebElement castElement : castElements) {
                        String name = castElement.findElement(By.cssSelector("div.name")).getText();
                        WebElement characterElement;
                        String character = "";

                        try {
                            characterElement = castElement.findElement(By.cssSelector("div.character"));
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
        } catch (Exception e) {}


        // 제작진 정보 저장
        Map<String, String> staffMap = new HashMap<>();
        try {
            WebElement staffList = WEB_DRIVER.findElement(By.cssSelector("div.person__staff"));
            if (!staffList.findElements(By.cssSelector("div.staff")).isEmpty()) {
//            log.info("Start Crawling Staffs");
                WebElement staffElement = staffList.findElement(By.cssSelector("div.staff"));
                List<WebElement> staffNameElements = staffElement.findElements(By.cssSelector("div.names__name"));

                for (WebElement staffNameElement : staffNameElements) {
                    String name = staffNameElement.findElement(By.tagName("span")).getText();
                    String position = "";
                    try {
                        position = staffElement.findElement(By.cssSelector("span.staff__title")).getText();
                    } catch (NoSuchElementException e) {
                        // character 요소가 존재하지 않는 경우, character 값을 빈 문자열로 유지
                    }
                    if (name.contains(".")) {
                        name = name.replace(".", "");
                    }
                    staffMap.put(name, position);
                }
            }
        } catch (Exception e) {}


        // Movie 객체 생성 및 데이터 설정

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setYear(year);
        movie.setSynopsis(synopsis);
        movie.setPosterImg(posterImgUrl);
        movie.setBackgroundImg(backgroundImgUrl);
        movie.setTagList(genreList);
        movie.setMetadata(metadataMap);
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
