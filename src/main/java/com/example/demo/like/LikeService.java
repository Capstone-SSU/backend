package com.example.demo.like;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.RecommendService;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.domain.User;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.userPreferenceHashtag.UserPreferenceHashtagService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class LikeService {
    private final UserPreferenceHashtagService preferenceHashtagService;
    private final LikeRepository likeRepository;
    private final RecommendService recommendService;

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
    public String changeLikeStatus(Lecture lecture, User user){
        Like existedLike = this.findLikeByLectureAndUser(lecture, user);
        if(existedLike!=null) { // 좋아요가 존재하는 경우
            if(existedLike.getLikeStatus()==1) { // 이미 눌려있는 경우
                existedLike.changeLikeStatus(0);
                preferenceHashtagService.updateUserPreferenceHashtag(user,lecture,-1);
                // 좋아요 상태 변경할 때마다 추천 연산 다시 하기
                recommendService.sendUserInfoAboutLike(user);
                return "like cancel";
            }
            else {
                existedLike.changeLikeStatus(1);
                preferenceHashtagService.updateUserPreferenceHashtag(user,lecture,1);
                // 좋아요 상태 변경할 때마다 추천 연산 다시 하기
                recommendService.sendUserInfoAboutLike(user);
                return "like success again";
            }
        }
        else {// 좋아요 처음 누른 경우
            Like like = new Like(lecture, user);
            this.saveLike(like);
            preferenceHashtagService.updateUserPreferenceHashtag(user,lecture,1);
            // 좋아요 상태 변경할 때마다 추천 연산 다시 하기
            recommendService.sendUserInfoAboutLike(user);
            return "like success";
        }

    }

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
