package com.example.demo.mypage;

import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.lecture.dto.DetailLectureResponse;
import com.example.demo.mypage.dto.LikedLecturesResponse;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.mypage.dto.LikedStudiesResponse;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.study.service.StudyPostService;
import com.example.demo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {
    private final LikeRepository likeRepository;
    private final LectureService lectureService;
    private final StudyPostService studyPostService;

    // 좋아요한 강의
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

    // 좋아요한 스터디
    public List<LikedStudiesResponse> getLikedStudies(User user){
        List<StudyPost> studies = likeRepository.findStudyLikeByUser(user);
        List<LikedStudiesResponse> likedStudies = new ArrayList<>();
        for(int i=0;i<studies.size();i++){
            StudyPost studyPost = studies.get(i);
            long studyPostId = studyPost.getStudyPostId();
            String studyTitle = studyPost.getStudyTitle();
            LocalDateTime studyCreatedDate = studyPost.getStudyCreatedDate();
            String studyLocation = studyPost.getStudyLocation();
            String studyRecruitState = (studyPost.getStudyRecruitStatus()==1) ? "모집중" : "모집완료";
            String studyCategoryName = studyPost.getStudyCategoryName();
            String nickname = user.getUserNickname();
            String profileImage = user.getUserProfileImg();
            LikedStudiesResponse likedStudiesResponse = new LikedStudiesResponse(studyPostId, studyTitle, studyCreatedDate, studyLocation, studyRecruitState, studyCategoryName, nickname, profileImage);
            likedStudies.add(likedStudiesResponse);
        }
        return likedStudies;
    }
}
