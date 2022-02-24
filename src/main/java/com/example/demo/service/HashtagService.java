package com.example.demo.service;

import com.example.demo.domain.Hashtag;
import com.example.demo.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashTagRepository hashTagRepository;

    public long saveHashtag(Hashtag hashtag){
        Hashtag savedHashtag = hashTagRepository.save(hashtag);
        return savedHashtag.getHashtagId();
    }

    public void findByKeyword(String keyword){
//        hashTagRepository.find
    }
}
