package org.project.simproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
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

    public void startCrawling() throws IOException, InterruptedException {

        String url = "https://m.kinolights.com/discover/explore";

        log.info("OTT 크롤링");

        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--remote-allow-origins=*");     // 웹 브라우저 Origin 허용
        chromeOptions.addArguments("--disable-popup-blocking");     // 팝업창 안띄우게 설정
        chromeOptions.addArguments("headless");                     // 브라우저 안띄우게 설정
        chromeOptions.addArguments("--disable-gpu");                // gpu 비활성화(headless 적용하기 위해 필요)

        webDriver = new ChromeDriver(chromeOptions);

        webDriver.get(url);

        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        // 넷플릭스 버튼 클릭
        WebElement movieItem = webDriver.findElement(By.xpath("//*[@id=\"contents\"]/section/div[2]/div/div/div/div[2]/button"));
        jse = (JavascriptExecutor) webDriver;
        jse.executeScript("arguments[0].click();", movieItem);

        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        System.out.println(webDriver.getTitle());

        // 10번 스크롤하여 나온 모든 컨텐츠의 링크 저장 및 출력(Test)
        scrollDownAndCollect(webDriver, jse);

        webDriver.close();
    }

    // 키노라이츠 사이트 등록 컨텐츠 중 넷플릭스 컨텐츠만 크롤링(Test)
    public static void scrollDownAndCollect(WebDriver driver, JavascriptExecutor js) throws InterruptedException {

        for (int i = 0; i < 5; i++) { // 5번 스크롤 내리기
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

            // 페이지 로딩까지 대기
            Thread.sleep(1000);


            List<WebElement> movieItems = driver.findElements(By.cssSelector("div.MovieItem"));

            List<WebElement> anchorTags = new ArrayList<>();
            for(WebElement movieItem : movieItems){
                anchorTags.add(movieItem.findElement(By.tagName("a")));
            }

            for (WebElement anchorTag : anchorTags) {
                System.out.println("찾은 <a> 태그: " + anchorTag.getAttribute("href"));
            }
            System.out.println();
            System.out.println();
        }
    }
}
