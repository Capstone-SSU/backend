package com.example.demo.repository;

import com.example.demo.domain.Hashtag;

import java.util.List;

public interface CustomHashtagRepository {
    List<Hashtag> findByKeyword(String keyword);
}
