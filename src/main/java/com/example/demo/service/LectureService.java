package com.example.demo.service;

import com.example.demo.domain.Lecture;
import com.example.demo.domain.Review;
import com.example.demo.domain.ReviewHashtag;
import com.example.demo.dto.LectureOnlyDto;
import com.example.demo.dto.LectureResponse;
import com.example.demo.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureService {
    private final LectureRepository lectureRepository;

    public long saveLecture(Lecture lecture){
        Lecture savedLecture = lectureRepository.save(lecture);
        return savedLecture.getLectureId();
    }

    // 전체 강의 조회
    public List<Lecture> findAllLectures (){
        List<Lecture> lectures = lectureRepository.findAll();
        System.out.println("lectures.size() = " + lectures.size());
        return lectures!=null?lectures:Collections.emptyList();
    }

    // 특정 강의 조회
    public LectureOnlyDto findById(Long lectureId){
        LectureResponse lectureResponse = new LectureResponse();
        Optional<Lecture> lecture = lectureRepository.findById(lectureId); // lecture 데이터 가져와서
        LectureOnlyDto lectureOnlyDto = new LectureOnlyDto(); // 원본객체 복사할 때 사용ㄴ
        BeanUtils.copyProperties(lecture.get(), lectureOnlyDto,"reviews", "user"); // 원본 객체, 복사 대상 객체
        System.out.println("lectureOnlyDto = " + lectureOnlyDto);
//        lectureResponse.setLectureOnlyDto(lecture.get(),lectureOnlyDto); // 원본 객체, 복사 대상 객체




//        lectureOnlyDto.setLecture(lecture.get().getLecture);
//
//        List<Review> reviews = lecture.get().getReviews();
//
//        for(int i=0;i<reviews.size();i++){ // reviews 목록
//            List<ReviewHashtag> reviewHashtags = reviews.get(i).getReviewHashtags();
//
//            JSONArray hashtags = new JSONArray();
////            List<String> hashtags = new ArrayList<>();
//            for(int j=0;j<reviewHashtags.size();j++) {
//                ReviewHashtag reviewHashtag = new ReviewHashtag();
//                long hashtagId = reviewHashtags.get(j).getReviewTagId();
//                String hashtagName = hashtagService.findById(hashtagId); // 해시태그 이름 받아오기
//                hashtags.appendElement(hashtagName);
////                hashtags.add(hashtagName);
//            }
////            System.out.println("hashtags = " + hashtags);
//
        return lectureOnlyDto;
    }

    // url 중복 조회용
    public Lecture findByUrl(String lectureUrl){
        Optional<Lecture> lecture = lectureRepository.findBylectureUrl(lectureUrl);
        return lecture.orElse(null);
    }
}
