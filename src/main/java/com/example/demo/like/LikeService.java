package com.example.demo.like;

import com.example.demo.domain.Lecture;
import com.example.demo.like.Like;
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
    public int changeLikeStatus(Like lectureLike, int likeStatus){
        if(likeStatus==0) // 취소한 상태에서 다시 누른 경우
            return likeRepository.updateLikeStatus(lectureLike, likeStatus+1);
        else // 좋아요 누른 상태에서 취소하는 경우
            return likeRepository.updateLikeStatus(lectureLike, likeStatus-1);
    }


    //studyPost와 user로 찾는게 있어야함
    public Like findLikeByStudyPostandUser(StudyPost post, User user){
        //user로 먼저 찾고 그 다음에 거기 내에서 studyPost로 찾는거
        List<Like> interestsByUser= likeRepository.findAllLikeByUser(user);

        //list에서 특정 StudyPost 한 개를 찾는 기능 다른데서도 사용되면 메소드로 따로 빼두기
        Like like =null;
        for (Like interest : interestsByUser) {
            if (interest.getStudyPost() == post) {
                like = interest;
                break;
            }
        }
        return like;
    }

    public List<Like> findAllLikesOnPost(StudyPost post){
        List<Like> likesOnPost=likeRepository.findAllLikeByStudyPost(post);
        Iterator<Like> itr=likesOnPost.iterator();
        while(itr.hasNext()){
            Like like=itr.next();
            if(like.getLikeStatus()==0){ //like==0이면 좋아요가 취소된 상태
                itr.remove();
            }
        }
        return likesOnPost;
    }
}
