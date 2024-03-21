package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.project.simproject.domain.Netflix;
import org.project.simproject.repository.NetflixRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-crawling.properties")
public class NetflixService {

    private WebDriver webDriver;

    @Value("${driver.chrome.driver_path}")
    private String WEB_DRIVER_PATH;

    @Value("${netflix.url}")
    private String NETFLIX_URL;

    @Value("${netflix.id}")
    private String NETFLIX_ID;

    @Value("${netflix.pwd}")
    private String NETFLIX_PWD;

    private final NetflixRepository netflixRepository;

    public void crawlingMostWatchedNetflix() {
        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

//        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
//        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
//        chromeOptions.addArguments("headless");                     // 브라우저 안띄우게 설정
//        chromeOptions.addArguments("--disable-gpu");                // gpu 비활성화(headless 적용하기 위해 필요)

        webDriver = new ChromeDriver(chromeOptions);

        webDriver.get(NETFLIX_URL);
        
        // 넷플릭스 로그인
        webDriver.findElement(By.xpath("//*[@id=\":r0:\"]")).sendKeys(NETFLIX_ID);
        webDriver.findElement(By.xpath("//*[@id=\":r3:\"]")).sendKeys(NETFLIX_PWD);
        webDriver.findElement(By.xpath("//*[@id=\"appMountPoint\"]/div/div/div[2]/div/form/button")).submit();

        webDriver.findElement(By.xpath("//*[@id=\"appMountPoint\"]/div/div/div[1]/div[1]/div[2]/div/div/ul/li[3]/div/a")).click();

        WebElement mostWatchedElement = webDriver.findElement(By.cssSelector("div[data-list-context='mostWatched']"));
        String category = mostWatchedElement.findElement(By.cssSelector(".row-header-title")).getText();
        log.info(category);

        List<WebElement> mostWatchedList = new ArrayList<>();

        int count = 1;  // Top10 크롤링을 위한 10개 카운팅
        int i = 0;
        while(count <= 10) {
            WebElement element = webDriver.findElement(By.cssSelector("div.slider-item.slider-item" + i++));
            String title = element.findElement(By.cssSelector("p.fallback-text")).getText();

            if(netflixRepository.existsNetflixByTitle(title)) {
                continue;
            }

            if(count == 5) {             // 5개 크롤링 후, 더 보기 버튼 클릭
                element.findElement(By.xpath("//span[@class='handle handleNext active']")).click();
                i = 0;
                continue;
            }

            String imageUrl = element.findElement(By.cssSelector("img.boxart-image-in-padded-container")).getText();

            Netflix netflix = Netflix.builder()
                    .title(title)
                    .category(category)
                    .rank((long) count++)
                    .build();

            netflixRepository.save(netflix);
        }

//        for (int i = 0; i <= 10; i++) {
//            WebElement mostViewElement = webDriver.findElement(By.cssSelector("div.slider-item.slider-item-" + i));
//
//            mostWatchedList.add(mostViewElement);
//
//            String title = mostViewElement.findElement(By.cssSelector("p.fallback-text")).getText();
//            String imageUrl = mostViewElement.findElement(By.cssSelector("img.boxart-image-in-padded-container")).getText();
//
//            Netflix netflix = Netflix.builder()
//                    .title(title)
//                    .category(category)
//                    .rank((long) i)
//                    .build();
//
//            netflixRepository.save(netflix);
//        }

        webDriver.quit();
    }

    public void getInfoByModal() {
        // Modal 창에서 정보를 알려주는 div 선택
        // Modal창을 선택하는 방법 추가 탐색 필요
        WebElement modalElement = webDriver.findElement(By.cssSelector("div.previewModal--detailsMetadata.detail-modal.has-smaller-buttons[data-uia='previewModal--detailsMetadata']"));
        String year = modalElement.findElement(By.cssSelector("div.year")).getText();
        List<String> actors = new ArrayList<>();            // 출연
        List<String> genres = new ArrayList<>();            // 장르
        List<String> seriesGenres = new ArrayList<>();      // 시리즈 특징

        // 출연자 이름 저장
        WebElement actorsElement = modalElement.findElement(By.cssSelector("div.previewModal--tags[data-uia='previewModal-tags-person']"));
        for (WebElement actorElement : actorsElement.findElements(By.cssSelector("span.tag-item")) ) {
            String actor = actorElement.getText();
            if(actor.equals("더 보기") || actor.isEmpty())
                break;
            actors.add(actor.replaceAll(",", ""));
        }

        // 장르 저장
        WebElement genresElement = modalElement.findElement(By.cssSelector("div.previewModal--tags[data-uia='previewModal--tags-genre']"));
        for (WebElement genreElement : genresElement.findElements(By.cssSelector("span.tag-item")) ) {
            String genre = genreElement.getText();
            if(genre.equals("더 보기") || genre.isEmpty())
                break;
            genres.add(genre.replaceAll(",", ""));
        }

        // 시리즈 특징 저장
        WebElement seriesGenresElement = modalElement.findElement(By.cssSelector("div.previewModal--tags[data-uia='previewModal-tags-genre']"));
        for (WebElement seriesGenreElement : seriesGenresElement.findElements(By.cssSelector("span.tag-item")) ) {
            String seriesGenre = seriesGenreElement.getText();
            if(seriesGenre.equals("더 보기") || seriesGenre.isEmpty())
                break;
            seriesGenres.add(seriesGenre.replaceAll(",", ""));
        }

    }

}
