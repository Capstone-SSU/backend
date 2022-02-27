package com.example.demo.hashtag.repository;

import com.example.demo.hashtag.Hashtag;

import java.util.List;

public interface CustomHashtagRepository {
    List<Hashtag> findByKeyword(String keyword);
}
