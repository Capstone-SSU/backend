package com.example.demo.controller;

import com.example.demo.domain.Hashtag;
import com.example.demo.dto.HashtagDto;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/hashtags")
public class HashtagController {
    private final HashtagService hashTagService;

    @PostMapping("/admin") // 해시태그 등록_관리자용
    public ResponseEntity<ResponseMessage> createHashTag(@RequestBody HashtagDto hashTagDto){
        String hashTagName = hashTagDto.getHashtag();
        Hashtag hashtag = new Hashtag(hashTagName);
        long hashTagId = hashTagService.saveHashtag(hashtag);
        return new ResponseEntity<>(new ResponseMessage(201, "해시태그가 등록되었습니다."), HttpStatus.CREATED);
    }

    @GetMapping("") // 단어가 포함된 모든 해시태그 조회
    public ResponseEntity<ResponseMessage> createHashTag(@RequestParam("keyword") String keyword){
        List<Hashtag> hashtagList = hashTagService.findByKeyword(keyword);
        for(int i=0;i<hashtagList.size();i++){
            System.out.println("hashtagList = " + hashtagList.get(i).getHashtagName());
        }
        return new ResponseEntity<>(ResponseMessage.withData(200, "해시태그가 조회되었습니다", hashtagList), HttpStatus.OK);
    }
}
