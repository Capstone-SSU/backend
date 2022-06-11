package com.example.demo.util;

import com.example.demo.hashtag.Hashtag;
import com.example.demo.hashtag.service.HashtagService;
import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Crawler {
    private final HashtagService hashtagService;
    private final LectureService lectureService;

    public static void main(String[] args) {
//        udemy("https://www.udemy.com/course/clean-code-js");
//        fastcampus("https://fastcampus.co.kr/dev_academy_kmt3");
//        inflearn("https://www.inflearn.com/course/%EC%A0%95%EB%8C%80%EB%A6%AC-%EC%8A%A4%EC%9C%84%ED%94%84%ED%8A%B8-%EA%B8%B0%EC%B4%88");
//        youtube("https://www.youtube.com/watch?v=6s51_S3aols");
//        nomadcoders("https://nomadcoders.co/nomadcoin");

        //1. url 중복 검증
        //2. url 에 들어있는 사이트 이름 식별 -> 크롤링 요청
        //3. 크롤링 요청 내부에서 saveLecture, manageHashtags 수행 (saveRequiredLecture 메소드 해당 파일 하단에 있음)
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
            tags.addAll(findHashtagsInTitle(rTitle,3));  //제목에서도 찾고
            if(tags.size()<3){
                tags.addAll(findHashtagsInTitle(content,10)
                        .stream()
                        .filter(h-> !tags.contains(h))
                        .limit(3-tags.size())
                        .collect(Collectors.toList()));
            }

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

    public void spartaCoding(String url){
        Document document;

        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
            //잘못된 url 연결 error throw
        }
        String siteName="스파르타코딩클럽";
        String lecturer="스파르타코딩클럽";
        String keywords = document.head().selectFirst("meta[name=keywords]").attr("content");
        System.out.println("keywords = " + keywords);
        List<String> hashtags = findHashtagsInTitle(keywords, 3);
        for(String h:hashtags){
            System.out.println("h = " + h);
        }

//        Elements select = document.body().select("h1.css-17lcj98");
        Elements select = document.body().select("section.css-8x5od0");
        for(Element e:select){
            System.out.println("e = " + e);
        }
        String title = document.head().selectFirst("title").text();
        String[] split = title.split("\\|");
        String rTitle = split[1].replaceFirst(" ", "");
        System.out.println("title = " + rTitle);
//        System.out.println("title = " + title);
//        String lecturer = document.selectFirst("h3.css-1juja8j").text();
//        System.out.println("lecturer = " + lecturer);

        String imageUrl = document.head().selectFirst("meta[property=og:image]").attr("content");
        String img="https://spartacodingclub.kr"+imageUrl;
        System.out.println("img = " + img);

    }

    public void projectlion(String url){
        Document document;

        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
            //잘못된 url 연결 error throw
        }
        String title = document.head().selectFirst("meta[property=og:title]").attr("content");
        int index = title.indexOf(":");
        String finalTitle = title.substring(1, index-1);
        System.out.println("finalTitle = " + finalTitle);
        String image = document.head().selectFirst("meta[property=og:image]").attr("content");
        System.out.println("image = " + image);
        String lecturer = "프로젝트 라이언";
        String siteName = "프로젝트 라이언";

        /**
         * 이거 갑자기 입문 왜안나오는지 확인 필요
            - UX/ UI 입문자를 위한 UX Discovery 인 경우에 R / ux / ui 가 나옴
            - 순서대로 나와서 자르다보니까 '입문' 이 나머지 3개보다 뒷번호라서 안나옴
            -> 그 강의를 잘 설명하는 해시태그가 안나올 수 있다는 한계점 아주..아주..
         */
        List<String> hashtags = findHashtagsInTitle(title, 3);
        for(String h : hashtags){
            System.out.println("h = " + h);
        }

        // 제목에 3개가 없으면 카테고리에서 추출하는데 카테고리가 없어요..

//        Lecture lecture = Lecture.builder()
//                .lecturer(lecturer)
//                .lectureUrl(url)
//                .lectureTitle(title)
//                .thumbnailUrl(img)
//                .siteName(siteName)
//                .build();

//        saveRequiredLecture(lecture,hashtags);
    }

    public void udemy(String url){
        Document document;

        try {
            document = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
            //잘못된 url 연결 error throw
        }

        String title = document.head().selectFirst("meta[name=title]").attr("content");
        System.out.println("title = " + title);
        String image = document.head().selectFirst("meta[property=og:image]").attr("content");
        System.out.println("image = " + image);
        String lecturer = document
                .selectFirst("a.udlite-btn.udlite-btn-large.udlite-btn-link.udlite-heading-md.udlite-text-sm.udlite-instructor-links span")
                .text();
        System.out.println("lecturer = " + lecturer);
        String siteName = "udemy";

        // 총 해시태그 담는 곳
        List<String> hashtags = new ArrayList<>();

        // 제목에서 해시태그 추출
        List<String> hashtagsInTitle = findHashtagsInTitle(title, 3);
        for(String h : hashtagsInTitle){
            hashtags.add(h);
        }

        //클린코드 자바스크립트인 경우 -> 자바, 자바 스크립트 출력
        
        // 제목에 3개 없으면 카테고리에서 추출
        /**
        * 개발 > 웹 개발 > JavaScript
        * 이런식으로 되어있어서 소분류부터 거꾸로 넣었는데 괜찮은가요..
        */
        if(hashtags.size() < 3){
            Elements elements = document.select("a.udlite-heading-sm");
            for(int i=elements.size()-1;i>=0;i--){
                if(hashtags.size() >= 3)
                    break;
                String tag=elements.get(i).text();

                // hashtags에 없는 경우에만 담기
                if(!hashtags.contains(tag))
                    hashtags.add(tag);
            }
        }
        for(String h : hashtags)
            System.out.println("h = " + h);

//        Lecture lecture = Lecture.builder()
//                .lecturer(lecturer)
//                .lectureUrl(url)
//                .lectureTitle(title)
//                .thumbnailUrl(img)
//                .siteName(siteName)
//                .build();

//        saveRequiredLecture(lecture,hashtags);
    }

    public void fastcampus(String url){
        Document document;

        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
            //잘못된 url 연결 error throw
        }

        String title = document.head().selectFirst("meta[property=og:title]").attr("content");
        int index = title.indexOf("|");
        String finalTitle = title.substring(1, index-1);
        System.out.println("finalTitle = " + finalTitle);

        String image = document.selectFirst("p.container__text-content.fc-h1-text").selectFirst("img").attr("src");
        System.out.println("image = " + image);
        String lecturer = "패스트 캠퍼스";

        // 총 해시태그 담는 곳
        List<String> hashtags = new ArrayList<>();

        List<String> hashtagsInTitle = findHashtagsInTitle(title, 3);
        for(String h : hashtagsInTitle){
            System.out.println("h = " + h);
        }

        /**
         *  제목에 3개가 없으면 카테고리에서 추출하는데 카테고리가 없어요..
         */

//        Lecture lecture = Lecture.builder()
//                .lecturer(lecturer)
//                .lectureUrl(url)
//                .lectureTitle(title)
//                .thumbnailUrl(img)
//                .siteName(siteName)
//                .build();

//        saveRequiredLecture(lecture,hashtags);
    }

    private List<String> findHashtagsInTitle(String title, int needCount){
        Pattern pattern = Pattern.compile("");
        //                    title.toLowerCase().contains(hashtag.getHashtagName().toLowerCase()
//                    title.matches(".*${hashtag}*.")

        return hashtagService.getAllHashtags()
                .stream()
//                .filter(pattern.asPredicate()) //
//                .filter(hashtag ->
//                    title.toUpperCase().contains(hashtag.getHashtagName().toUpperCase())
//                )
                .filter(hashtag ->
                        title.contains(hashtag.getHashtagName())
                )
                .limit(needCount)
                .map(Hashtag::getHashtagName) // ContainingClass::methodName
                .collect(Collectors.toList());
    }

    private void saveRequiredLecture(Lecture lecture, List<String> hashtags){
        lectureService.saveLecture(lecture);
        lectureService.manageHashtag(hashtags, lecture);
    }
}
