package com.example.demo.like;

import com.example.demo.lecture.Lecture;
import com.example.demo.like.Like;
import com.example.demo.roadmap.RoadMap;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.User;
import com.example.demo.like.repository.LikeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public void saveLike(Like like){
        likeRepository.save(like);
    }

    public Like findLikeById(Long id){
        Optional<Like> like = likeRepository.findById(id);
        return like.orElse(null);
    }

    // 특정 유저가 특정 강의에 좋아요 누른지 확인
    public Like findLikeByLectureAndUser(Lecture lecture, User user){
        Optional<Like> like = likeRepository.findLikeByLectureAndUser(lecture, user);
        return like.orElse(null);
    }

    // 좋아요 상태 변경하기
//    public int changeLikeStatus(Like lectureLike, int likeStatus){
//        if(likeStatus==0) // 취소한 상태에서 다시 누른 경우
//            return likeRepository.updateLikeStatus(lectureLike, likeStatus+1);
//        else // 좋아요 누른 상태에서 취소하는 경우
//            return likeRepository.updateLikeStatus(lectureLike, likeStatus-1);
//    }

    //studyPost와 user로 찾는게 있어야함
    public Like findLikeByStudyPostandUser(StudyPost post, User user){
        //user로 먼저 찾고 그 다음에 거기 내에서 studyPost로 찾는거
        Optional<Like> foundLike=likeRepository.findLikeByUserAndStudyPost(user,post);
        return foundLike.orElse(null);
    }

    public Integer getLikeCountOnStudyPost(StudyPost post){
        List<Like> likesOnPost=likeRepository.findLikeByStudyPost(post);
        return likesOnPost.size();
    }

    public Like findLikeByRoadmapAndUser(User user, RoadMapGroup group){
        return likeRepository.findLikeByRoadmapGroupAndUser(group,user);
    }

    public Integer getLikeCountOnRoadmap(RoadMapGroup group){
        return likeRepository.findLikeByRoadmap(group).size();
    }



}
