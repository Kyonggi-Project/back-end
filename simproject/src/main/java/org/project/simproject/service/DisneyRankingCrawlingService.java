package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.project.simproject.domain.OTT;
import org.project.simproject.domain.RankingInfo;
import org.project.simproject.repository.mongoRepo.OTTRepository;
import org.project.simproject.repository.mongoRepo.RankingInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-crawling.properties")
public class DisneyRankingCrawlingService {
    private WebDriver webDriver;

    private JavascriptExecutor jse;

    private final RankingInfoRepository rankingInfoRepository;

    private final OTTRepository ottRepository;

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

        webDriver.get(crawlingUrl);

        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        WebElement rankingItem = webDriver.findElement(By.xpath("//*[@id=\"disneyDailyRankingButton\"]"));
        jse = (JavascriptExecutor) webDriver;
        jse.executeScript("arguments[0].click();", rankingItem);

        Thread.sleep(1000);

        if(rankingInfoRepository.existsRankingInfoByOtt(ott)){
            createRanking(webDriver, ott);
        }
        else{
            updateRanking(webDriver, ott);
        }

        webDriver.close();
    }

    public void createRanking(WebDriver driver, String ott){
        int count = 0;
        List<OTT> rankingList = new ArrayList<>();

        List<WebElement> ranking = driver.findElements(By.cssSelector("p.info__title"));

        for(WebElement rank : ranking){
            if(ottRepository.existsOTTByTitle(rank.getText())){
                OTT movie = ottRepository.findOTTByTitle(rank.getText());
                rankingList.add(movie);
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
    }

    public void updateRanking(WebDriver driver, String ott){
        int count = 0;
        List<WebElement> ranking = driver.findElements(By.cssSelector("p.info__title"));
        RankingInfo rankingInfo = rankingInfoRepository.findRankingInfoByOtt(ott);

        rankingInfo.deleteRankingList();

        for(WebElement rank : ranking){
            if(ottRepository.existsOTTByTitle(rank.getText())){
                OTT movie = ottRepository.findOTTByTitle(rank.getText());
                rankingInfo.addRankingList(movie);
                count++;
                if(count == 10) break;
            }
        }
    }
}
