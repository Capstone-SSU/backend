package com.example.demo.controller;

import com.example.demo.domain.HashTag;
import com.example.demo.dto.HashTagDto;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.service.HashTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/hashtags")
public class HashTagController {
    private final HashTagService hashTagService;

    @PostMapping("/admin") // 해시태그 등록_관리자용
    public ResponseEntity<ResponseMessage> createHashTag(@RequestBody HashTagDto hashTagDto){
        String hashTagName = hashTagDto.getHashtag();
        HashTag hashTag = new HashTag(hashTagName);
        long hashTagId = hashTagService.saveHashTag(hashTag);
        return new ResponseEntity<>(new ResponseMessage(201, "해시태그가 등록되었습니다."), HttpStatus.CREATED);
    }

//    @GetMapping("/:keyword")

}
