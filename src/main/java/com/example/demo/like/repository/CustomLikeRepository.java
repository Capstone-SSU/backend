package com.example.demo.like.repository;

import com.example.demo.lecture.Lecture;
import com.example.demo.like.Like;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.domain.User;

import java.util.List;

public interface CustomLikeRepository {
    int updateLikeStatus(Like like, int likeStatus);
    List<Like> findLikeByLecture(Lecture lecture); // 강의글 좋아요 가져오기
    List<Like> findLikeByStudyPost(StudyPost post);
    List<Like> findLikeByRoadmap(RoadMapGroup roadMapGroup);
    List<Lecture> findLectureLikeByUser(User user);
    List<StudyPost> findStudyLikeByUser(User user);
    List<RoadMapGroup> findRoadmapLikeByUser(User user);
    Like findLikeByRoadmapGroupAndUser(RoadMapGroup group, User user);
}
