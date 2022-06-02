package com.example.demo.hashtag.service;

import com.example.demo.hashtag.Hashtag;
import com.example.demo.hashtag.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public List<Hashtag> getAllHashtags(){
        List<Hashtag> hashtags = hashtagRepository.findAll();
        System.out.println("hashtags = " + hashtags);
        return hashtags;
    }

    public long saveHashtag(Hashtag hashtag){
        Hashtag savedHashtag = hashtagRepository.save(hashtag);
        return savedHashtag.getHashtagId();
    }

    public String findById(long hashtagId){
        Optional<Hashtag> hashtag = hashtagRepository.findById(hashtagId);
        return hashtag.get().getHashtagName();
    }

    public Hashtag findByName(String hashtagName){
        Optional<Hashtag> hashtag = hashtagRepository.findByHashtagName(hashtagName);
        return hashtag.orElse(null);
    }

    public List<Hashtag> findByKeyword(String keyword){
        return hashtagRepository.findByKeyword(keyword);
    }


}
