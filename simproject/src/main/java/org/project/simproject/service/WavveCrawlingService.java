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
import org.project.simproject.domain.OTTContents;
import org.project.simproject.domain.RankingInfo;
import org.project.simproject.repository.mongoRepo.OTTContentsRepository;
import org.project.simproject.repository.mongoRepo.RankingInfoRepository;
//import org.springframework.beans.factory.annotation.Value;
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
public class WavveCrawlingService {
    private WebDriver webDriver;

    private final OTTContentsRepository ottRepository;

    private final RankingInfoRepository rankingInfoRepository;

    private JavascriptExecutor jse;

    @Value("${driver.chrome.driver_path}")
    private String WEB_DRIVER_PATH;

    @Value("${wavveLoginid}")
    private String LOGINID;

    @Value("${wavveLoginpwd}")
    private String LOINGPWD;

    @Value("${wavve.url}")
    private String url;

    private RankingInfo series;

    private RankingInfo movie;

    private String Popular = "'오늘의 TOP 20'";
    private String Moive = "'오늘의 영화 TOP 20'";

    public void crawlingMostWatchedWavve() throws InterruptedException {
        login(url);

        scroll();

        webDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        series = seriesRanking(Popular);

        Thread.sleep(1000);

        movie = movieRanking(Moive);

        for (RankingInfo result : rankingInfoRepository.findAll()) {
            System.out.println(result.getCategory());
            List<OTTContents> ottContents = result.getRankingList();
            System.out.println(result.getRankingList().size());
            for(OTTContents ott : ottContents) {
                if(ott == null) {
                    continue;
                }
                System.out.println(ott.getTitle());
            }
        }
        webDriver.quit();
    }
    @Transactional
    public RankingInfo seriesRanking(String CrawlingStr) throws InterruptedException {
        RankingInfo seriesRankingInfo = getRankingInfo(CrawlingStr);

        if (rankingInfoRepository.existsRankingInfoByOttAndCategory(seriesRankingInfo.getOtt(), seriesRankingInfo.getCategory())) { // 유무 검사 내가 boolean 값으로 만들어뒀는데 이제 푸시할 거
            RankingInfo Info = rankingInfoRepository.findRankingInfoByOttAndCategory(seriesRankingInfo.getOtt(), seriesRankingInfo.getCategory());      // 해당 RankingInfo 객체 찾기
            rankingInfoRepository.delete(Info);
        }
        return rankingInfoRepository.save(seriesRankingInfo);
    }
    @Transactional
    public RankingInfo movieRanking(String CrawlingStr) throws InterruptedException {
        RankingInfo movieRankingInfo = getRankingInfo(CrawlingStr);

        if (rankingInfoRepository.existsRankingInfoByOttAndCategory(movieRankingInfo.getOtt(), movieRankingInfo.getCategory())) { // 유무 검사
            RankingInfo Info = rankingInfoRepository.findRankingInfoByOttAndCategory(movieRankingInfo.getOtt(), movieRankingInfo.getCategory());      // 해당 RankingInfo 객체 찾기
            rankingInfoRepository.delete(Info);
        }
        return rankingInfoRepository.save(movieRankingInfo);
    }

    public RankingInfo getRankingInfo(String CrawlingStr) throws InterruptedException {
        List<String> list = getList(CrawlingStr);

        webDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        WebElement top20Container = webDriver.findElement(By.xpath("//*[contains(text(),"+CrawlingStr+")]"));

        List<OTTContents> ottContents = new ArrayList<>();
        for(String ottTitle:list) {
            if(ottRepository.existsOTTByTitle(ottTitle)) {
                ottContents.add(ottRepository.findOTTByTitle(ottTitle));
            }
        }
        log.info("OTTContents>>>>>"+ottContents);
        RankingInfo RankingInfo = new RankingInfo("Wavve",top20Container.getText(),ottContents);
        return RankingInfo;
    }
    public List<String> getList(String CrawlingStr) throws InterruptedException {
        List<String> list = new ArrayList<>();
        WebElement top20Container = webDriver.findElement(By.xpath("//*[contains(text(),"+CrawlingStr+")]"));
        jse = (JavascriptExecutor) webDriver;
        jse.executeScript("arguments[0].scrollIntoView(true);",top20Container);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebElement top20Contain = top20Container.findElement(By.xpath("//*[contains(text(),"+CrawlingStr+")]/../../../div[2]/div[1]/div[1]"));
        List<WebElement> top20s = new ArrayList<WebElement>();
        for(int j = 1; j <= 20; j++) {
            top20s.add(top20Contain.findElement(By.cssSelector("div:nth-child("+j+") > div:nth-child(1) > a > div:nth-child(2) > div:nth-child(1) > picture")));
            webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        }
        for(WebElement top : top20s) {
            WebElement result = top.findElement(By.tagName("img"));
            list.add(result.getAttribute("alt"));
        }
        log.info(list);
        return list;
    }
    public void popupclose() throws InterruptedException {
        WebElement popup = webDriver.findElement(By.xpath("//*[contains(text(),'전면 팝업')]/../div[1]/a"));
        jse = (JavascriptExecutor) webDriver;
        jse.executeScript("arguments[0].click();", popup);

    }
    public void login(String url) throws InterruptedException {
        init();
        log.info("wavve 로그인");
        webDriver.get(url);
        log.info("페이지 접속후 1초 로딩");
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        webDriver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/main/div/div[1]/form/fieldset/ul[1]/li[1]/label/input")).sendKeys(LOGINID);
        webDriver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/main/div/div[1]/form/fieldset/ul[1]/li[2]/label/input")).sendKeys(LOINGPWD);

        WebElement login = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/main/div/div[1]/form/fieldset/div/a"));
        jse = (JavascriptExecutor) webDriver;
        jse.executeScript("arguments[0].click();", login);
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        popupclose();
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }
    public void init() {
        log.info("WEB_DRIVER_PATH: {}", WEB_DRIVER_PATH);
        log.info("LOGINID: {}", LOGINID);
        log.info("LOINGPWD: {}", LOINGPWD);
        log.info("URL : {}", url);

        log.info("OTT 크롤링");
        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
//        chromeOptions.addArguments("headless");                     // 브라우저 안띄우게 설정
        chromeOptions.addArguments("--disable-gpu");                // gpu 비활성화(headless 적용하기 위해 필요)

        webDriver = new ChromeDriver(chromeOptions);
    }

    public void scroll() throws InterruptedException {
        while (true){
            long currentScrollHeight = (long) jse.executeScript("return document.body.scrollHeight;");
            long currentWindowHeight = (long) jse.executeScript("return window.innerHeight;");
            long currentScrollY = (long) jse.executeScript("return window.scrollY;");

            // 스크롤이 불가능할 때까지 무한 스크롤
            if (currentScrollHeight > (currentWindowHeight + currentScrollY)){
                jse.executeScript("window.scrollTo(0, document.body.scrollHeight);");

                Thread.sleep(1000);
            }
            else break;
        }
    }
}
