package com.example.demo.like.repository;

import com.example.demo.lecture.Lecture;
import com.example.demo.like.Like;
import com.example.demo.roadmap.RoadMap;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long>, CustomLikeRepository {
    List<Like> findAllLikeByUser(User user); // user기반으로 모든 관심글 찾아오기
    List<Like> findAllLikeByStudyPost(StudyPost post); //한 스터디글에 대한 모든 좋아요 정보 가져오기
    Optional<Like> findLikeByLectureAndUser(Lecture lecture, User user);
    Optional<Like> findLikeByUserAndStudyPost(User user, StudyPost post);
}
