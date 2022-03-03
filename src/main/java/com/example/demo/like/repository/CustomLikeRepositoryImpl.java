package com.example.demo.like.repository;
import com.example.demo.lecture.Lecture;
import com.example.demo.like.Like;
import com.example.demo.roadmap.QRoadMap;
import com.example.demo.roadmap.RoadMap;
import com.example.demo.study.domain.StudyPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.demo.like.QLike.like;

@Repository
@RequiredArgsConstructor
public class CustomLikeRepositoryImpl implements CustomLikeRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public int updateLikeStatus(Like likes, int likeStatus){
        jpaQueryFactory
                .update(like)
                .set(like.likeStatus, likeStatus)
                .where(like.likeId.eq(likes.getLikeId()))
                .execute();
        return likeStatus;
    }

    @Override
    public List<Like> findLikeByLecture(Lecture lecture) {
        return jpaQueryFactory
                .selectFrom(like)
                .where(like.likeStatus.eq(1))
                .fetch();
    }

    @Override
    public List<Like> findLikeByStudyPost(StudyPost post) {
        return jpaQueryFactory
                .selectFrom(like)
                .where(like.studyPost.eq(post),like.likeStatus.eq(1))
                .fetch();
    }
}
