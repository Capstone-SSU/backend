package com.example.demo.mypage;

import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.lecture.dto.DetailLectureResponse;
import com.example.demo.lecture.dto.LikedLecturesResponse;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {
    private final LikeRepository likeRepository;
    private final LectureService lectureService;

    public List<LikedLecturesResponse> getLikedLectures(User user){
        List<Lecture> lectures = likeRepository.findLectureLikeByUser(user);
        List<LikedLecturesResponse> likedLectures = new ArrayList<>();
        for(int i=0;i<lectures.size();i++){
            Lecture lecture = lectures.get(i);
            DetailLectureResponse detailLectureResponse = lectureService.getLecture(lecture.getLectureId(), user.getUserId());
            LikedLecturesResponse likedLecturesResponse = new LikedLecturesResponse();
            BeanUtils.copyProperties(detailLectureResponse, likedLecturesResponse,"reviewCnt", "likeCnt", "reviews"); // 원본 객체, 복사 대상 객체
            likedLectures.add(likedLecturesResponse);
        }
        return likedLectures;
    }
}
