package com.example.demo.hashtag;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.hashtag.service.HashtagService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = { "Lecture Hashtag"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/hashtags")
public class HashtagController {
    private final HashtagService hashtagService;

    @PostMapping("/admin") // 해시태그 등록_관리자용
    public ResponseEntity<ResponseMessage> createHashtagAdmin(@RequestBody HashtagDto hashTagDto){
        String hashtagName = hashTagDto.getHashtag();
        Hashtag existedHashtag = hashtagService.findByName(hashtagName);
        if(existedHashtag != null) // 이미 있는 해시태그라면
            return new ResponseEntity<>(new ResponseMessage(409, "이미 등록된 해시태그입니다."), HttpStatus.CONFLICT);
        Hashtag hashtag = new Hashtag(hashtagName);
        long hashTagId = hashtagService.saveHashtag(hashtag);
        return new ResponseEntity<>(new ResponseMessage(201, "해시태그가 등록되었습니다."), HttpStatus.CREATED);
    }

    @GetMapping("") // 단어가 포함된 모든 해시태그 조회
    public ResponseEntity<ResponseMessage> getAllHashtags(@RequestParam("keyword") String keyword){
        List<Hashtag> hashtagList = hashtagService.findByKeyword(keyword);
        if(hashtagList.isEmpty())
            return new ResponseEntity<>(new ResponseMessage(200, "직접 해시태그를 입력해주세요"), HttpStatus.OK);

        for(int i=0;i<hashtagList.size();i++){
            System.out.println("hashtagList = " + hashtagList.get(i).getHashtagName());
        }
        return new ResponseEntity<>(ResponseMessage.withData(200, "해시태그가 조회되었습니다", hashtagList), HttpStatus.OK);
    }
//    @PostMapping("")
//    public ResponseEntity<ResponseMessage> createHashtag(@RequestBody String lectureUrl){
//
//    }
}
