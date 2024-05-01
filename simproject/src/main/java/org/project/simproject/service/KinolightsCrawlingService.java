package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.repository.mongoRepo.OTTContentsRepository;
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

    @Value("${kinolights.url}")
    private String KINOLIGHTS_URL;

    String[] OTT_ARRAY = new String[7];

    private final OTTContentsRepository ottContentsRepository;

    @Transactional
    public void crawlingMovies() throws InterruptedException {
        init();

        for (int i = 2; i < 7; i++) {
            if (i == 3) continue;   // Tving 건너뛰기

            try {
                WEB_DRIVER.get(KINOLIGHTS_URL + "/discover/explore");
                Thread.sleep(5000);
                WebElement buttonElement = WEB_DRIVER.findElement(By.xpath("//*[@id=\"contents\"]/section/div[2]/div/div/div/div[" + i + "]/button"));
                JS_EXECUTOR.executeScript("arguments[0].click();", buttonElement);
            } catch (NoSuchElementException e) {
                // 버튼 클릭 실패 시, 재시도
                WEB_DRIVER.get(KINOLIGHTS_URL + "/discover/explore");
                Thread.sleep(5000);
                WebElement buttonElement = WEB_DRIVER.findElement(By.xpath("//*[@id=\"contents\"]/section/div[2]/div/div/div/div[" + i + "]/button"));
                JS_EXECUTOR.executeScript("arguments[0].click();", buttonElement);
            }
            String ott = OTT_ARRAY[i];

            log.info(ott + " Crawling Start");

            scroll();

            // DB 중복검사
            List<WebElement> movieList = WEB_DRIVER.findElements(By.cssSelector("div.MovieItem.grid"));
            List<WebElement> crawlingList = new ArrayList<>();

            for (WebElement movieElement : movieList) {
                String title = movieElement.findElement(By.cssSelector("div.title")).getText();
                if (!ottContentsRepository.existsOTTByTitle(title)) {
                    crawlingList.add(movieElement);
                } else {
                    log.info(title + " is already crawled");
                }
            }
            List<String> hrefList = collectHref(crawlingList);

            int count = 0;

            for (String hrefLink : hrefList) {
                WEB_DRIVER.get(hrefLink);

                if (!WEB_DRIVER.findElements(By.cssSelector("div.modal-layer")).isEmpty()) {
                    closeModal();
                }

                String title = null;
                try {
                    title = getTitle();
                } catch (Exception e) {
                    log.error("Error in getTitle(): " + e.getMessage());
                    continue;
                }

                if (ottContentsRepository.existsOTTByTitle(title)) {
                    log.info("[" + ott + "] " + count++ + ": [" + title + "] (in DB)");
                    OTTContents updateMovie = ottContentsRepository.findOTTByTitle(title);
                    updateMovie.addOTTList(ott);
                    ottContentsRepository.save(updateMovie);
                    continue;
                }

                OTTContents movie = crawlingInfo(count++, title, ott);
                movie.setScore(0);
                movie.setReviewCount(0);
                movie.setRating(0);
                ottContentsRepository.save(movie);
            }
        }

        // 웹 드라이버 종료
        WEB_DRIVER.quit();
    }

    @Transactional
    public void todayNewMovies() {
        init();

        List<String> newMovieTitleList = new ArrayList<>();
        int count = 0;

        for (int i = 2; i < 7; i++) {
            if (i == 3) continue;   // Tving 건너뛰기

            WEB_DRIVER.get(KINOLIGHTS_URL + "/new");
//            Thread.sleep(5000);
            WAIT.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"contents\"]/section[2]/div/div/div/div/div[" + i + "]/button")));
            WebElement buttonElement = WEB_DRIVER.findElement(By.xpath("//*[@id=\"contents\"]/section[2]/div/div/div/div/div[" + i + "]/button"));
            JS_EXECUTOR.executeScript("arguments[0].click();", buttonElement);
            String ott = OTT_ARRAY[i];

            scroll();

            WebElement newMovieSection = WEB_DRIVER.findElement(By.cssSelector("section.new-streaming-wrap"));
            if (newMovieSection.findElement(By.xpath("./h5/span[2]")).getText().equals("오늘")) {
                log.info("오늘의 " + ott + " 신작 크롤링 시작");
                WebElement ottNewMovieWrap = newMovieSection.findElement(By.cssSelector("div.contents-wrap"));
                List<WebElement> ottNewMovies = ottNewMovieWrap.findElements(By.cssSelector("div.MovieItem.xs.eg-flick-panel"));
                List<String> hrefList = collectHref(ottNewMovies);
                for (String hrefLink : hrefList) {
                    WEB_DRIVER.get(hrefLink);

                    if (!WEB_DRIVER.findElements(By.cssSelector("div.modal-layer")).isEmpty()) {
                        closeModal();
                    }

                    String title = getTitle();

                    // 이미 오늘의 신작 크롤링 시, 크롤링된 작품일 경우 OTT만 추가
                    if (newMovieTitleList.contains(title)) {
                        OTTContents updateMovie = ottContentsRepository.findOTTByTitle(title);
                        updateMovie.addOTTList(ott);
                        ottContentsRepository.save(updateMovie);
                        continue;
                    }

                    OTTContents movie = crawlingInfo(count++, title, ott);

                    /*DB에 해당 Movie가 저장되어 있다면,
                    DB에 저장되어 있는 Score, ReviewCount, Rating을 이용해 새로 저장*/
                    if (ottContentsRepository.existsOTTByTitle(title)) {
                        OTTContents updateMovie = ottContentsRepository.findOTTByTitle(title);
                        movie.setScore(updateMovie.getScore());
                        movie.setReviewCount(updateMovie.getReviewCount());
                        movie.setRating(updateMovie.getRating());
                        ottContentsRepository.delete(updateMovie);    // 새로 저장하게 되므로, 이전 기록은 삭제
                    } else {    // 만약 DB에 존재하지 않는다면, 모두 0으로 초기화
                        movie.setScore(0);
                        movie.setReviewCount(0);
                        movie.setRating(0);
                    }
                    ottContentsRepository.save(movie);
                    newMovieTitleList.add(title);
                }
            }
        }

        log.info("Crawling New Movies Success");
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

        WAIT = new WebDriverWait(WEB_DRIVER, Duration.ofSeconds(10));

        OTT_ARRAY[2] = "Netflix";
        OTT_ARRAY[4] = "Coupang Play";
        OTT_ARRAY[5] = "Wavve";
        OTT_ARRAY[6] = "Disney Plus";

        log.info("Initialize Success");
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

    public List<String> collectHref(List<WebElement> elementList) {

//        log.info("Get MovieList Success");
//        log.info("Start get Info");

        List<String> hrefList = new ArrayList<>();

        for (WebElement element : elementList) {
            hrefList.add(element.findElement(By.tagName("a")).getAttribute("href"));
        }

        return hrefList;
    }

    public void closeModal() {
        WebElement modalElement = WEB_DRIVER.findElement(By.cssSelector("div.modal-layer"));
        modalElement.findElement(By.cssSelector("div.container__footer > button")).click();
        log.info("Closing Modal");
    }

    public String getTitle() {
        WebElement titleElement = WAIT.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.movie-title-wrap h2.title-kr")));
        return titleElement.getText();
    }

    public OTTContents crawlingInfo(int count, String title, String ott) {
        String posterImgUrl;
        while(true){
            posterImgUrl = WEB_DRIVER.findElement(By.className("movie-poster")).getAttribute("data-src");
            if(posterImgUrl != null) break;
        }

        String backgroundImgUrl = WEB_DRIVER.findElement(By.cssSelector("div.backdrop img")).getAttribute("data-src");

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
                WAIT.until(ExpectedConditions.elementToBeClickable(buttonElement));
                JS_EXECUTOR.executeScript("arguments[0].click();", buttonElement);
//                log.info("Clicked Synopsis Button");
            }
            synopsis = synopsisElement.getText();
//            log.info(synopsis);
        }

        // 메타데이터
        HashMap<String, String> metadataMap = new HashMap<>();
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
                String[] genres = itemBody.split(",\\s*");
                genreList.addAll(Arrays.asList(genres));
                continue;
            }
            metadataMap.put(itemTitle, itemBody);
        }

        // 배우 정보 저장
        HashMap<String, String> actorCharacterMap = new HashMap<>();
        if (!WEB_DRIVER.findElements(By.cssSelector("div.person.list__avatar")).isEmpty()) {
//            log.info("Start Crawling Actors");
            WebElement actorList = WEB_DRIVER.findElement(By.cssSelector("div.person__actor"));
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

        // 제작진 정보 저장
        HashMap<String, String> staffMap = new HashMap<>();
        if (!WEB_DRIVER.findElements(By.cssSelector("div.staff")).isEmpty()) {
//            log.info("Start Crawling Staffs");
            WebElement staffList = WEB_DRIVER.findElement(By.cssSelector("div.person__staff"));
            WebElement staffElement = staffList.findElement(By.cssSelector("div.staff"));
            List<WebElement> staffNameElements = staffElement.findElements(By.cssSelector("a.names__name"));

            for (WebElement staffNameElement : staffNameElements) {
                String name = staffNameElement.findElement(By.tagName("span")).getText();
//                log.info("Staff: " + name);
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

        // Movie 객체 생성 및 데이터 설정
        OTTContents movie = OTTContents.builder()
                .title(title)
                .year(year)
                .posterImg(posterImgUrl)
                .backgroundImg(backgroundImgUrl)
                .synopsis(synopsis)
                .genreList(genreList)
                .metaData(metadataMap)
                .actorList(actorCharacterMap)
                .staffList(staffMap)
                .build();
        movie.addOTTList(ott);

        log.info("[" + ott + "] " + count + ": [" + title + "] (" + year + ")");

        return movie;
    }
}
