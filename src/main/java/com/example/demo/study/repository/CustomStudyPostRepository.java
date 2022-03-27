package com.example.demo.study.repository;

import com.example.demo.mypage.dto.MyStudiesResponse;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomStudyPostRepository {
    List<StudyPost> findPostsWithFilter(String[] categories, String[] keywords, String location, Integer recruitStatus, String sort);
    List<MyStudiesResponse> findByUser(User user);
}
