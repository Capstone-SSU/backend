package com.example.demo.service;

import com.example.demo.domain.Hashtag;
import com.example.demo.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashTagRepository;

    public long saveHashtag(Hashtag hashtag){
        Hashtag savedHashtag = hashTagRepository.save(hashtag);
        return savedHashtag.getHashtagId();
    }

    public String findById(long hashtagId){
        Optional<Hashtag> hashtag = hashTagRepository.findById(hashtagId);
        return hashtag.get().getHashtagName();
    }

    public Hashtag findByName(String hashtagName){
        Optional<Hashtag> hashtag = hashTagRepository.findByHashtagName(hashtagName);
        return hashtag.orElse(null);
    }

    public List<Hashtag> findByKeyword(String keyword){
        return hashTagRepository.findByKeyword(keyword);
    }

}
