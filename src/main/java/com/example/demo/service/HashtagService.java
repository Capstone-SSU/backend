package com.example.demo.service;

import com.example.demo.domain.Hashtag;
import com.example.demo.repository.CustomHashtagRepository;
import com.example.demo.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashTagRepository hashTagRepository;

    public long saveHashtag(Hashtag hashtag){
        Hashtag savedHashtag = hashTagRepository.save(hashtag);
        return savedHashtag.getHashtagId();
    }

    public List<Hashtag> findByKeyword(String keyword){
        return hashTagRepository.findByKeyword(keyword);
    }
}
