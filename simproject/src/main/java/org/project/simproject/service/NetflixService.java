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
import org.project.simproject.repository.NetflixRepository;
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

    private final NetflixRepository netflixRepository;

    public void crawlingMostWatchedNetflix() throws InterruptedException {
        init();

        WEB_DRIVER.get(NETFLIX_URL);

        login();

        Thread.sleep(5000);

        WebElement mostWatchedElement = WEB_DRIVER.findElement(By.cssSelector("div[data-list-context='mostWatched']"));
        String category = mostWatchedElement.findElement(By.cssSelector(".row-header-title")).getText();
        log.info(category);

        List<WebElement> mostWatchedList = new ArrayList<>();
        List<String> mostWatchedNames = new ArrayList<>();

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
            if (mostWatchedNames.contains(title) || title.isEmpty())
                continue;
            mostWatchedNames.add(title);
            log.info(count++ + ": " + title);

            /*
            String imageUrl = element.findElement(By.cssSelector("img.boxart-image-in-padded-container")).getText();

            Netflix netflix = Netflix.builder()
                    .title(title)
                    .category(category)
                    .ranking((long) count++)
                    .build();

            netflixRepository.save(netflix);*/
        }

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
