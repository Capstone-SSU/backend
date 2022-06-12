package com.example.demo.hashtag;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.hashtag.service.HashtagService;
import com.example.demo.util.Crawler;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = {"Lecture Hashtag"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/hashtags")
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

    @GetMapping("/test")
    public void test(){
        String url = "";
        // 노마드코더
//        url="https://nomadcoders.co/javascript-for-beginners";
        // 프로젝트 라이언
//        url="https://projectlion.io/courses/technology/uxd";
        // 유데미
//        url = "https://www.udemy.com/course/clean-code-js";
        // 유튜브
//        String url="https://youtu.be/kWiCuklohdY";
        // 패스트 캠퍼스
//        url="https://fastcampus.co.kr/dev_academy_kmt3";

        // 생활코딩
        url = "https://opentutorials.org/course/3086/18311";


//        crawler.nomadcoders(url);
//        crawler.youtube(url);
//        crawler.projectlion(url);
//        crawler.udemy(url);
//        crawler.fastcampus(url);
        crawler.codingEverybody(url);
    }
}
