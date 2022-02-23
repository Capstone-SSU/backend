package com.example.demo.service;

import com.example.demo.domain.HashTag;
import com.example.demo.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashTagService {
    private final HashTagRepository hashTagRepository;

    public long saveHashTag(HashTag hashTag){
        HashTag savedHashTag = hashTagRepository.save(hashTag);
        return savedHashTag.getHashTagId();
    }
}
