package org.project.simproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.project.simproject.domain.OTTContents;
import org.project.simproject.domain.RankingInfo;
import org.project.simproject.repository.mongoRepo.OTTContentsRepository;
import org.project.simproject.repository.mongoRepo.RankingInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final int[] score = {25,18,15,12,10,8,6,4,2,1};
    public void crawlingMostWatchedWavve() throws InterruptedException {
        try {
            run();
        } catch (TimeoutException | NoSuchElementException e) {
            webDriver.quit();
            run();
        }
    }
    public void run() throws InterruptedException {
        final String Popular = "'오늘의 TOP 20'";
        final String Moive = "'오늘의 영화 TOP 20'";
        login(url);

        Thread.sleep(1000);

        scroll();

        webDriver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
        Thread.sleep(1000);

        series = seriesRanking(Popular);

        Thread.sleep(1000);

        movie = movieRanking(Moive);

        webDriver.quit();
    }
    @Transactional
    public RankingInfo seriesRanking(String CrawlingStr) throws InterruptedException {
        RankingInfo seriesRankingInfo = getRankingInfo(CrawlingStr);

        if (rankingInfoRepository.existsRankingInfoByOttAndCategory(seriesRankingInfo.getOtt(), seriesRankingInfo.getCategory())) { // 유무 검사 내가 boolean 값으로 만들어뒀는데 이제 푸시할 거
            RankingInfo Info = rankingInfoRepository.findRankingInfoByOttAndCategory(seriesRankingInfo.getOtt(), seriesRankingInfo.getCategory());      // 해당 RankingInfo 객체 찾기
            rankingInfoRepository.delete(Info);
        }

        for(int i = 0; i < score.length; i++) {
            OTTContents ottContents = seriesRankingInfo.getRankingList().get(i);
            if (ottContents == null) {
                continue;
            } else {
                int rank = score[i] * 5;
                ottContents.updateRakingScore(rank);
            }
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

        for(int i = 0; i < score.length; i++) {
            OTTContents ottContents = movieRankingInfo.getRankingList().get(i);
            List<OTTContents> seriesRankingTop10 = series.getRankingList().subList(0,10);
            if (ottContents == null) {
                continue;
            } else {
                if (!seriesRankingTop10.contains(ottContents)) {
                    movieRankingInfo.getRankingList().get(i).updateRakingScore((int) (score[i] * 0.7 * 5));
                }
                else {
                    int seriesRank = seriesRankingTop10.indexOf(ottContents);
                    int rank = (int) (score[i] * 0.7 * 5);
                    int max_value = (rank > score[seriesRank] * 5) ? rank : score[seriesRank] * 5;
                    movieRankingInfo.getRankingList().get(i).updateRakingScore(max_value);
                }
            }
        }
        return rankingInfoRepository.save(movieRankingInfo);
    }

    public RankingInfo getRankingInfo(String CrawlingStr) throws InterruptedException {
        List<String> list = getList(CrawlingStr);
        webDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        WebElement top20Container = webDriver.findElement(By.xpath("//*[contains(text(),"+CrawlingStr+")]"));
        List<OTTContents> ottContents = new ArrayList<>();
        int year = 0;

        for(String ottTitle:list) {
            String replaceStr = ottTitle.replace(" ","");
            if(ottRepository.existsOTTContentsByTitle(ottTitle)) {
                List<OTTContents> ott = ottRepository.findAllOTTContentsByTitle(ottTitle);
                if (ott.size() == 1) {
                    OTTContents oneOTT = ott.get(0);
                    ottContents.add(oneOTT);
                }
                else {
                    year = ott.stream().mapToInt(OTTContents::getYear).max().orElse(year);
                    List<String> temp = new ArrayList<>();
                    OTTContents eqaulsYearOTT = new OTTContents();
                    for (OTTContents contents : ott) {
                        if (temp.contains(contents.getTitle())) {
                            break;
                        } else if (contents.getOttList().contains("Wavve")) {
                            ottContents.add(contents);
                            temp.add(contents.getTitle());
                        } else if(contents.getYear() == year) {
                            eqaulsYearOTT = contents;
                        }
                    }
                    if(!temp.contains(eqaulsYearOTT.getTitle()) && eqaulsYearOTT.getTitle() != null) {
                        ottContents.add(eqaulsYearOTT);
                    }
                }
            } else if (ottRepository.findOTTContentsBySubtitleListContaining(replaceStr) != null) {
                OTTContents ott = ottRepository.findOTTContentsBySubtitleListContaining(replaceStr);
                ottContents.add(ott);
            } else if (ottRepository.findOTTContentsBySubtitleListContaining(ottTitle) != null) {
                OTTContents ott = ottRepository.findOTTContentsBySubtitleListContaining(replaceStr);
                ottContents.add(ott);
            } else if (ottRepository.findOTTContentsBySubtitleListContaining(replaceStr.replace("-",":")) != null) {
                OTTContents ott = ottRepository.findOTTContentsBySubtitleListContaining(replaceStr.replace("-",":"));
                ottContents.add(ott);
            } else if(ottRepository.findAllBySubtitleListContainsIgnoreCase(replaceStr+"시즌") != null){
                String seasonString = "시즌";
                OTTContents seasonOTT = getSeason(replaceStr+seasonString);
                if(seasonOTT == null) {
                    if(ottRepository.findOTTContentsByTitleContaining(getPrefixBeforeName(ottTitle)) != null) {
                        getAnotherPatternCheck(ottContents,ottTitle);
                    } else {
                        emptyOTT(ottContents,ottTitle);
                    }
                } else {
                    ottContents.add(seasonOTT);
                }
            }  else {
                emptyOTT(ottContents,ottTitle);
            }
        }
        RankingInfo RankingInfo = new RankingInfo("Wavve",top20Container.getText(),ottContents);
        return RankingInfo;
    }

    public void getAnotherPatternCheck(List<OTTContents> ottContents,String ottTitle) {
        OTTContents ott = ottRepository.findOTTContentsByTitleContaining(getPrefixBeforeName(ottTitle));
        String OTTReplace = ottTitle.replace(" ","");
        Pattern pattern = Pattern.compile("^(!+)(.*?)(!+)$");
        Matcher matcher = pattern.matcher(OTTReplace);
        String filteringStr = "";
        if(matcher.find()) {
            filteringStr = matcher.group(1);
        }
        if(OTTReplace.contains(""+ott.getYear())) {
            ottContents.add(ott);
        } else if(ott.getSubtitleList().contains(filteringStr)) {
            ottContents.add(ott);
        } else {
            emptyOTT(ottContents,ottTitle);
        }
    }

    public OTTContents getSeason(String replaceStr) {

        List<OTTContents> seasonOTT = ottRepository.findAllBySubtitleListContainsIgnoreCase(replaceStr);
        int year = 0;
        year = seasonOTT.stream().mapToInt(OTTContents::getYear).max().orElse(year);
        for(OTTContents season : seasonOTT) {
            log.info("Section Title : "+season.getTitle());
            if(season.getYear() == year) {
                return season;
            }
        }
        return null;
    }

    public void emptyOTT(List<OTTContents> ottContents, String ottTitle) {
        log.info("Null Title : " + ottTitle);
        ottContents.add(null);
    }

    public List<String> getList(String CrawlingStr) throws InterruptedException {
        List<String> list = new ArrayList<>();
        WebElement top20Container = webDriver.findElement(By.xpath("//*[contains(text(),"+CrawlingStr+")]"));
        jse = (JavascriptExecutor) webDriver;
        jse.executeScript("arguments[0].scrollIntoView(true);",top20Container);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebElement top20Contain = top20Container.findElement(By.xpath("//*[contains(text(),"+CrawlingStr+")]/../../../div[2]/div[1]/div[1]"));
        List<WebElement> top20s = new ArrayList<>();
        for(int j = 1; j <= 20; j++) {
            top20s.add(top20Contain.findElement(By.cssSelector("div:nth-child("+j+") > div:nth-child(1) > a > div:nth-child(2) > div:nth-child(1) > picture")));
            webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        }
        for(WebElement top : top20s) {
            WebElement result = top.findElement(By.tagName("img"));
            list.add(result.getAttribute("alt"));
        }
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
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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
        log.info("OTT 크롤링");
        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
        chromeOptions.addArguments("headless");                     // 브라우저 안띄우게 설정
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
    public static String getPrefixBeforeName (String input) {
        // 특정 문자열 찾기
        int index = input.indexOf("(");
        if (index != -1) { // 특정 문자열이 발견되면
            return input.substring(0, index).trim(); // 발견된 문자열 앞까지 잘라서 반환
        } else { // 특정 문자열이 없으면
            return input.trim(); // 원본 문자열 그대로 반환
        }
    }
}
