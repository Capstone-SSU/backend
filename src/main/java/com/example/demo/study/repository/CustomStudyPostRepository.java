package com.example.demo.study.repository;

import com.example.demo.mypage.dto.MyStudiesResponse;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomStudyPostRepository {
    //컨텐트 기반 -> 검색어 조회 시 -> 검색어가 여러개 올 수도 있음
//    List<StudyPost> findPostsByTitleKeywords(Set<String> keywords);
//    //제목 기반 -> 검색어 조회 시 -> 검색어가 여러개 올 수도 있음
//    List<StudyPost> findPostsByContentKeywords(Set<String> keywords);
    //지역 기반 -> 지역 조회 시 -> 지역은 하나만 검색 -> jpa repository 로 뺴두기
    //카테고리 기반 -> 카테고리 조회 시 -> 카테고리가 몇개가 들어올 지 모르므로 동적쿼리?
    List<StudyPost> findPostsByTest(String[] categories, String[] keywords, String location);
    List<MyStudiesResponse> findByUser(User user);
}
