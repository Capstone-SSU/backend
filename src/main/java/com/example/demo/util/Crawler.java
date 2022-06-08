package com.example.demo.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Crawler {


    public static void main(String[] args) {

        inflearn("https://www.inflearn.com/course/%EC%A0%95%EB%8C%80%EB%A6%AC-%EC%8A%A4%EC%9C%84%ED%94%84%ED%8A%B8-%EA%B8%B0%EC%B4%88");
//        youtube("https://youtu.be/jdTsJzXmgU0");
        youtube("https://www.youtube.com/watch?v=6s51_S3aols");
    }

    private static void inflearn(String url){
        Document document=null;

        try {
            document = Jsoup.connect(url).get();

        } catch (IOException e) {
            e.printStackTrace();
            return;
            //잘못된 url 연결 error throw
        }

        String img = document.selectFirst("div.cd-header__thumbnail img").attr("src");
        System.out.println("img = " + img);
        String title = document.selectFirst("div.cd-header__title").text();
        System.out.println("title = " + title);
        String lecturer = document.selectFirst("a.cd-header__instructors--main").text();
        System.out.println("lecturer = " + lecturer);
        Elements elements = document.select("a.cd-header__tag");
        for(Element e:elements){
            String tag=e.text();
            System.out.println("tag = " + tag);
        }
    }

    private static void youtube(String url){

        Document document=null;

        try {
            document = Jsoup.connect(url).get();

        } catch (IOException e) {
            e.printStackTrace();
            return;
            //잘못된 url 연결 error throw
        }

        List<String> tags=new ArrayList<>();
        Element body = document.body();
        String title = body.selectFirst("div#watch7-content meta[itemprop=name]").attr("content");
        System.out.println("title = " + title);
        if(title.contains("#")){
            String[] split = title.split("#");
            //0번 idx 이후부터는 전부 다 해시태그임
            for(int i=1;i<split.length;i++){
                String replace = split[i].replace(" ", "");
                tags.add(replace);
                if(tags.size()==3)
                    break;
            }
        }

        String lecturer = body.select("div#watch7-content link[itemprop=name]").attr("content");
        System.out.println("lecturer = " + lecturer);
        String img = body.select("div#watch7-content link[itemprop=thumbnailUrl]").attr("href");
        System.out.println("img = " + img);

//        System.setProperty("webdriver.chrome.driver","C:\\Users\\user\\Downloads\\chromedriver_win32\\chromedriver.exe");
//        WebDriver webDriver=new ChromeDriver();
//
//        if(tags.size()>=3)
//            return;
//
//        webDriver.get(url);
//        try {
//            TimeUnit.SECONDS.sleep(5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        List<WebElement> elements =webDriver.findElements(By.cssSelector("a.yt-simple-endpoint.style-scope.yt-formatted-string"));
//        for(WebElement e:elements){
//            if(e.getText().contains("#")){
//                String tag = e.getText().replace("#", "");
//                System.out.println("tag = " + tag);
//            }
//
//            if(!e.getText().contains("#"))
//                break;
//
//        }
//
//        webDriver.close();
//        webDriver.quit();

    }

    private static void nomadcoders(String url){
        Document document=null;

        try {
            document = Jsoup.connect(url).get();

        } catch (IOException e) {
            e.printStackTrace();
            return;
            //잘못된 url 연결 error throw
        }
        Element body = document.body();

    }


}
