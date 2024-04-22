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
import org.project.simproject.domain.OTT;
import org.project.simproject.repository.mongoRepo.OTTRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-crawling.properties")
public class CrawlingService {
    private WebDriver webDriver;

    private JavascriptExecutor jse;

    @Value("${driver.chrome.driver_path}")
    private String WEB_DRIVER_PATH;

    @Value("${crawling.url}")
    private String crawlingUrl;

    private final OTTRepository ottRepository;

    public void startCrawling() throws IOException, InterruptedException {
        log.info("키노라이츠 디즈니 플러스 크롤링");

        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
        chromeOptions.addArguments("headless");                     // 브라우저 안띄우게 설정
        chromeOptions.addArguments("--disable-gpu");                // gpu 비활성화(headless 적용하기 위해 필요)

        webDriver = new ChromeDriver(chromeOptions);

        webDriver.get(crawlingUrl);

        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        // 디즈니 플러스 작품 선택
        WebElement movieItem = webDriver.findElement(By.xpath("//*[@id=\"contents\"]/section/div[2]/div/div/div/div[6]/button"));
        jse = (JavascriptExecutor) webDriver;
        jse.executeScript("arguments[0].click();", movieItem);

        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        // 크롤링 시작
        crawlingStart(webDriver, jse);

        webDriver.close();
    }

    // 크롤링을 시작하는 메소드
    public void crawlingStart(WebDriver driver, JavascriptExecutor js) throws InterruptedException {

        while (true){
            long currentScrollHeight = (long) js.executeScript("return document.body.scrollHeight;");
            long currentWindowHeight = (long) js.executeScript("return window.innerHeight;");
            long currentScrollY = (long) js.executeScript("return window.scrollY;");

            // 스크롤이 불가능할 때까지 무한 스크롤
            if (currentScrollHeight > (currentWindowHeight + currentScrollY)){
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

                Thread.sleep(1000);
                continue;
            }

            // 작품 목록 하이퍼링크 저장
            List<WebElement> movieItems = driver.findElements(By.cssSelector("div.MovieItem"));

                List<WebElement> anchorTags = new ArrayList<>();
                for(WebElement movieItem : movieItems){
                    anchorTags.add(movieItem.findElement(By.tagName("a")));
            }

            List<String> hrefs = new ArrayList<>();
            for (WebElement anchorTag : anchorTags) {
                hrefs.add(anchorTag.getAttribute("href"));
            }

            // 각 작품에 대한 정보 크롤링
            for (String href : hrefs) {
                OTT ott;
                driver.get(href);
                driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

                HashMap<String, String> basicInfo = new HashMap<>();
                // 작품 기본 정보 크롤링(제목, 년도, 포스터, 백그라운드 이미지)
                if(!driver.findElements(By.className("movie-title-wrap")).isEmpty()){
                    WebElement title = driver.findElement(By.className("title-kr"));
                    if(ottRepository.existsOTTByTitle(title.getText())){
                        ott = ottRepository.findOTTByTitle(title.getText());
                        if (!ott.getOttList().contains("Disney Plus")) {
                            ott.addOTTList("Disney Plus");
                            ottRepository.save(ott);
                        }
                        continue;
                    }
                    else {
                        basicInfo = getTitleAndYear(driver);
                    }
                }

                String synopsis = "";
                // 줄거리 크롤링
                if (!driver.findElements(By.cssSelector("div.text span")).isEmpty()) {
                    synopsis = getSynopsis(driver, js);
                }

                HashMap<String, String> metadataMap = new HashMap<>();
                List<String> genreList = new ArrayList<>();
                // 작품 태그 크롤링
                if(!driver.findElements(By.cssSelector("ul.metadata")).isEmpty()){
                    getTags(driver, metadataMap, genreList);
                }

                HashMap<String, String> actorList = new HashMap<>();
                // 출연진 크롤링
                if(!driver.findElements(By.cssSelector("div.person.list__avatar")).isEmpty()){
                    actorList = getActors(driver);
                }

                HashMap<String, String> staffList = new HashMap<>();
                // 제작진 크롤링
                if(!driver.findElements(By.cssSelector("div.staff")).isEmpty()){
                    staffList = getStaffs(driver);
                }

                ott = OTT.builder()
                        .title(basicInfo.get("title"))
                        .year(Integer.parseInt(basicInfo.get("year")))
                        .posterImg(basicInfo.get("posterImg"))
                        .backgroundImg(basicInfo.get("backgroundImg"))
                        .synopsis(synopsis)
                        .tagList(genreList)
                        .metaData(metadataMap)
                        .actorList(actorList)
                        .staffList(staffList)
                        .score(0.0)
                        .reviewCount(0)
                        .rating(0.0f)
                        .build();
                ott.addOTTList("Disney Plus");

                ottRepository.save(ott);
                log.info("DB 저장 완료 : " + basicInfo.get("title"));
            }
            break;
        }
    }

    // 작품 기본정보 크롤링하는 메소드
    public HashMap<String, String> getTitleAndYear(WebDriver driver){
        WebElement title = driver.findElement(By.className("title-kr"));
        WebElement year = driver.findElements(By.className("metadata-item")).get(1);
        String poster = driver.findElement(By.className("movie-poster")).getAttribute("data-src");
        String backgroundImage = driver.findElement(By.cssSelector("div.backdrop img")).getAttribute("data-src");

        HashMap<String, String> basicInfo = new HashMap<>();
        basicInfo.put("title", title.getText());
        basicInfo.put("year", year.getText());
        basicInfo.put("posterImg", poster);
        basicInfo.put("backgroundImg", backgroundImage);

        return basicInfo;
    }

    // 줄거리 크롤링하는 메소드
    public String getSynopsis(WebDriver driver, JavascriptExecutor js){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100));

        try { // 줄거리가 길 경우, 더보기 버튼 클릭한 후, 줄거리 크롤링
            WebElement synopsis = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.text span")));

            if(synopsis.getText().contains("...")){
                WebElement more = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.synopsis__text-wrap div.text button")));
                js.executeScript("arguments[0].click();", more);

            }
            synopsis = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.text span")));
            return synopsis.getText();
        } catch (Exception e) {
            WebElement synopsis = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.text span")));
            return synopsis.getText();
        }
    }

    // 작품 태그 크롤링하는 메소드
    public void getTags(WebDriver driver, HashMap<String, String> metadataMap, List<String> genreList){
        WebElement metadata = driver.findElement(By.cssSelector("ul.metadata"));
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
    }

    // 작품에 출연한 출연진 크롤링(최대 5명)
    public HashMap<String, String> getActors(WebDriver driver){
        int count = 0;
        HashMap<String, String> actorList = new HashMap<>();

        WebElement list = driver.findElement(By.cssSelector("div.person__actor"));
        List<WebElement> actors = list.findElements(By.cssSelector("div.person.list__avatar"));

        if(!actors.isEmpty()){
            for(WebElement actor : actors){
                count++;
                String name = actor.findElement(By.cssSelector("div.name")).getText();
                if(name.contains(".")) name = name.replace(".", "");
                try{
                    WebElement characterElement = actor.findElement(By.cssSelector("div.character"));
                    String character = characterElement.getText();
                    if(character.contains(".")) character = character.replace(".", "");

                    actorList.put(name, character);

                } catch (Exception e){
                    actorList.put(name, "");
                }
                if(count == 5) break;
            }
        }

        return actorList;
    }

    // 작품에 참여한 제작진 크롤링(최대 5명)
    public HashMap<String, String> getStaffs(WebDriver driver){
        int count = 0;
        HashMap<String, String> staffList = new HashMap<>();

        WebElement list = driver.findElement(By.cssSelector("div.person__staff"));
        WebElement director = list.findElements(By.cssSelector("div.staff")).get(0);
        List<WebElement> staffs = director.findElements(By.cssSelector("a.names__name"));

        if(!staffs.isEmpty()){
            for(WebElement staff : staffs){
                count++;
                String name = staff.findElement(By.tagName("span")).getText();
                if(name.contains(".")) name = name.replace(".", "");
                try{
                    WebElement characterElement = director.findElement(By.cssSelector("span.staff__title"));
                    String character = characterElement.getText();
                    if(character.contains(".")) character = character.replace(".", "");

                    staffList.put(name, character);

                } catch (Exception e){
                    staffList.put(name, "");
                }
                if(count == 5) break;
            }
        }

        return staffList;
    }
}
