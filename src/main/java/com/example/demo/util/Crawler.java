package com.example.demo.util;

import com.example.demo.hashtag.Hashtag;
import com.example.demo.hashtag.service.HashtagService;
import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class Crawler {
    private final HashtagService hashtagService;
    private final LectureService lectureService;


    public static void main(String[] args) {

        //1. url 중복 검증
        //2. url 에 들어있는 사이트 이름 식별 -> 크롤링 요청
        //3. 크롤링 요청 내부에서 saveLecture, manageHashtags 수행 (saveRequiredLecture 메소드 해당 파일 하단에 있음)

//        inflearn("https://www.inflearn.com/course/%EC%A0%95%EB%8C%80%EB%A6%AC-%EC%8A%A4%EC%9C%84%ED%94%84%ED%8A%B8-%EA%B8%B0%EC%B4%88");
//        youtube("https://www.youtube.com/watch?v=6s51_S3aols");
//        nomadcoders("https://nomadcoders.co/nomadcoin");
    }

    public void inflearn(String url){
        Document document;

        try {
            document = Jsoup.connect(url).get();

        } catch (IOException e) {
            e.printStackTrace();
            return;
            //잘못된 url 연결 error throw
        }

        List<String> hashtags=new ArrayList<>();

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
            hashtags.add(tag);
        }

        Lecture lecture = Lecture.builder()
                .lecturer(lecturer)
                .lectureUrl(url)
                .lectureTitle(title)
                .thumbnailUrl(img)
                .siteName("인프런")
                .build();

//        saveRequiredLecture(lecture,hashtags);
    }

    public void youtube(String url){

        Document document;

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

        if(tags.size()<3){
            List<String> hashtagsInTitle = findHashtagsInTitle(title, 3 - tags.size());
            tags.addAll(hashtagsInTitle);
        }

        String lecturer = body.select("div#watch7-content link[itemprop=name]").attr("content");
        System.out.println("lecturer = " + lecturer);
        String img = body.select("div#watch7-content link[itemprop=thumbnailUrl]").attr("href");
        System.out.println("img = " + img);

        for(String t:tags){
            System.out.println("t = " + t);
        }


        Lecture lecture = Lecture.builder()
                .lectureTitle(title)
                .siteName("유튜브")
                .thumbnailUrl(img)
                .lectureUrl(url)
                .lecturer(lecturer)
                .build();

//        saveRequiredLecture(lecture,tags);

    }

    public void nomadcoders(String url){
        String baseUrl="https://nomadcoders.co/courses";

        Document document;
        Document base;

        try {
            document = Jsoup.connect(url).get();
            base=Jsoup.connect(baseUrl).get();

        } catch (IOException e) {
            e.printStackTrace();
            return;
            //잘못된 url 연결 error throw
        }

        String title = document.head().selectFirst("meta[property=og:title]").attr("content");
        String[] split = title.split("노마드 코더");
        String rTitle=split[0].substring(0,split[0].length()-3);
        System.out.println("title = " + rTitle);
        String content = document.head().selectFirst("meta[property=og:description]").attr("content");
        System.out.println("content = " + content);

        String lecturer="니꼴라스";
        String siteName="노마드코더";
        String imgUrl;
        List<String> tags=new ArrayList<>();


        if(content.contains(",")){
            Arrays.stream(content.split(","))
                    .limit(3)
                    .forEach(t->tags.add(t.replace(" ","")));
        }else if(content.contains("+")){
            Arrays.stream(content.split("\\+"))
                    .limit(3)
                    .forEach(t->tags.add(t.replace(" ","")));
        }else{
            tags.addAll(findHashtagsInTitle(rTitle,3));
            if(tags.size()<3)
                tags.addAll(findHashtagsInTitle(content,3- tags.size()));
        }

        String[] split1 = url.split("/");
        String last = split1[split1.length - 1];

        Elements div = base.select("div.sc-7257b669-0.kKJInu.flex.flex-col.relative.rounded-lg.items-center");
        for(Element e:div){
            if(e.selectFirst("a").attr("href").contains(last)){
                Elements img = e.getElementsByTag("img");
                imgUrl="https://nomadcoders.co"+img.get(1).attr("src");
                System.out.println("imgUrl = " + imgUrl);
                break;
            }

        }

        for(String t:tags){
            System.out.print("t = " + t+" ");
        }

    }

    private List<String> findHashtagsInTitle(String title, int needCount){
        return hashtagService.getAllHashtags()
                .stream()
                .filter(h->title.contains(h.getHashtagName()))
                .limit(needCount)
                .map(Hashtag::getHashtagName)
                .collect(Collectors.toList());
    }

    private void saveRequiredLecture(Lecture lecture, List<String> hashtags){
        lectureService.saveLecture(lecture);
        lectureService.manageHashtag(hashtags, lecture);
    }


}
