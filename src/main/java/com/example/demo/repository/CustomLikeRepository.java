package com.example.demo.repository;

import com.example.demo.domain.Like;

public interface CustomLikeRepository {
    int updateLikeStatus(Like like, int likeStatus);
}
