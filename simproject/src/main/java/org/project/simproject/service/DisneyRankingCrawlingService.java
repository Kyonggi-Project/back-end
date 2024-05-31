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
public class DisneyRankingCrawlingService {
    private WebDriver webDriver;

    private WebDriverWait wait;

    private int[] scoreMatrix = {25, 18, 15, 12, 10, 8, 6, 4, 2, 1};

    private int rate = 4;

    private final RankingInfoRepository rankingInfoRepository;

    private final OTTContentsRepository ottRepository;

    @Value("${driver.chrome.driver_path}")
    private String WEB_DRIVER_PATH;

    @Value("${disney.ranking.url}")
    private String crawlingUrl;

    public void startCrawling(String ott) throws InterruptedException {
        log.info("디즈니 플러스 TOP 10 크롤링");

        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
        chromeOptions.addArguments("headless");                     // 브라우저 안띄우게 설정
        chromeOptions.addArguments("--disable-gpu");                // gpu 비활성화(headless 적용하기 위해 필요)

        webDriver = new ChromeDriver(chromeOptions);

        wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));

        webDriver.get(crawlingUrl);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p.info__title")));

        List<String> hrefs = getHrefs(webDriver);

        if(!rankingInfoRepository.existsRankingInfoByOtt(ott)){
            createRanking(hrefs, ott);
        }
        else{
            RankingInfo rankingInfo = rankingInfoRepository.findRankingInfoByOtt(ott);
            updateRanking(hrefs, rankingInfo);
        }


        webDriver.close();
    }

    @Transactional
    public void createRanking(List<String> hrefs, String ott){
        int count = 0;
        List<OTTContents> rankingList = new ArrayList<>();

        for(String href : hrefs){
            if(ottRepository.existsOTTContentsByHref(href)){
                OTTContents movie = ottRepository.findOTTContentsByHref(href);
                movie.updateRakingScore(scoreMatrix[count] * rate);
                rankingList.add(movie);
                ottRepository.save(movie);
                count++;
                if(count == 10) break;
            }
        }

        RankingInfo rankingInfo = RankingInfo.builder()
                .ott(ott)
                .category("Disney 영화 및 시리즈 TOP 10")
                .rankingList(rankingList)
                .build();

        rankingInfoRepository.save(rankingInfo);
        log.info("디즈니 플러스 rankinginfo 추가 완료");
    }

    @Transactional
    public void updateRanking(List<String> hrefs, RankingInfo rankingInfo){
        int count = 0;

        rankingInfo.deleteRankingList();

        for(String href : hrefs){
            if(ottRepository.existsOTTContentsByHref(href)){
                OTTContents movie = ottRepository.findOTTContentsByHref(href);
                movie.updateRakingScore(scoreMatrix[count] * rate);
                rankingInfo.addRankingList(movie);
                ottRepository.save(movie);
                count++;
                if(count == 10) break;
            }
        }

        rankingInfoRepository.save(rankingInfo);

        log.info("디즈니 플러스 rankinginfo 업데이트 완료");
    }

    public List<String> getHrefs(WebDriver driver){
        List<WebElement> movieItems = driver.findElements(By.cssSelector("div.ranking"));

        List<WebElement> anchorTags = new ArrayList<>();
        for(WebElement movieItem : movieItems){
            anchorTags.add(movieItem.findElement(By.tagName("a")));
        }

        List<String> hrefs = new ArrayList<>();
        for (WebElement anchorTag : anchorTags) {
            hrefs.add(anchorTag.getAttribute("href"));
        }

        return hrefs;
    }
}
