package com.example.demo.hashtag;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.hashtag.service.HashtagService;
import com.example.demo.lecture.LectureService;
import com.example.demo.util.Crawler;
import com.sun.mail.iap.Response;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Api(tags = {"Lecture Hashtag"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/hashtags")
@Slf4j
@EnableAsync
public class HashtagController {
    private final HashtagService hashtagService;
    private final Crawler crawler;

    @PostMapping("/admin") // 해시태그 등록_관리자용
    public ResponseEntity<ResponseMessage> createHashtagAdmin(@RequestBody HashtagDto hashTagDto){
        String hashtagName = hashTagDto.getHashtag();
        Hashtag existedHashtag = hashtagService.findByName(hashtagName);
        if(existedHashtag != null) // 이미 있는 해시태그라면
            return new ResponseEntity<>(new ResponseMessage(409, "이미 등록된 해시태그입니다."), HttpStatus.CONFLICT);
        Hashtag hashtag = new Hashtag(hashtagName);
        hashtagService.saveHashtag(hashtag);
        return new ResponseEntity<>(new ResponseMessage(201, "해시태그가 등록되었습니다."), HttpStatus.CREATED);
    }

    @GetMapping("") // 단어가 포함된 모든 해시태그 조회 (강의 등록시 자동으로 키워드를 포함하는 해시태그 목록 조회하기 위해서)
    public ResponseEntity<ResponseMessage> getAllHashtags(@RequestParam(value = "keyword", required = false) String keyword){
        if(keyword==null){
            List<Hashtag> hashtagList = hashtagService.getAllHashtags();
            System.out.println("hashtagList = " + hashtagList); // 여기서는 reviewHashtags 안나오는데 왜 리턴할때만 나올까
            List<HashtagDto> hashtags = new ArrayList<>();
            for(int i=0;i<hashtagList.size();i++){
                HashtagDto hashtagDto = new HashtagDto();
                hashtagDto.setHashtag(hashtagList.get(i).getHashtagName());
                hashtags.add(hashtagDto);
            }
            return new ResponseEntity<>(ResponseMessage.withData(200, "모든 해시태그가 조회되었습니다", hashtags), HttpStatus.OK);
        }
        List<Hashtag> hashtagList = hashtagService.findByKeyword(keyword);
        if(hashtagList.isEmpty())
            return new ResponseEntity<>(new ResponseMessage(200, "직접 해시태그를 입력해주세요"), HttpStatus.OK);

        return new ResponseEntity<>(ResponseMessage.withData(200, "'"+keyword+"'키워드가 포함된 해시태그가 조회되었습니다", hashtagList), HttpStatus.OK);
    }

    //if else 를 안쓰려면 프론트에서 요청 url 에 따라 다른 controller 를 호출하는 방법 뿐,,,
    @GetMapping("/test")
    public int test(@RequestParam String request_url) throws Exception {
        String url = request_url;
        Long lectureId=null;
        //lectureId 가 null 로 넘어가면 테스트용 크롤링이니까 실제 데이터베이스에 강의 저장 X


        if(url.contains("nomadcoders")){
            crawler.nomadcoders(url, lectureId);
        }else if(url.contains("projectlion")){
            crawler.projectlion(url, lectureId);
        }else if(url.contains("udemy")){
            crawler.udemy(url, lectureId);
        }else if(url.contains("youtu")){
            crawler.youtube(url, lectureId);
        }else if(url.contains("fastcampus")){
            crawler.fastcampus(url, lectureId);
        }else if(url.contains("inflearn")){
            crawler.inflearn(url, lectureId);
        }else if(url.contains("spartacoding")){
            crawler.spartaCoding(url, lectureId);
        }else if(url.contains("opentutorials")){
            crawler.codingEverybody(url, lectureId);
        }else{
            return Response.BAD;
        }

        return Response.OK;
    }


}
