package com.example.demo.study.util;

import com.example.demo.study.domain.StudyPost;
import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;

//좋아요 개수 기준으로 스터디글 정렬하기 위해 만든 클래스
@Builder
@Getter
public class StudyPostLikeCalc{
    private StudyPost studyPost;
    private Integer likeCount;
    public StudyPostLikeCalc(StudyPost post,Integer likeCount){
        this.studyPost=post;
        this.likeCount=likeCount;
    }

//    @Override
//    public int compareTo(StudyPostLikeCalc post) {
//       if(post.likeCount<likeCount){
//           return 1;
//       }else if(post.likeCount>likeCount){
//           return -1;
//       }else{
//           return (-1) * (post.getStudyPost().getStudyPostId().compareTo(studyPost.getStudyPostId());
//       }
//    }


}
