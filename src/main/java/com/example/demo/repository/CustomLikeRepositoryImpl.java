package com.example.demo.repository;
import com.example.demo.domain.Like;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static com.example.demo.domain.QLike.like;

@Repository
@RequiredArgsConstructor
public class CustomLikeRepositoryImpl implements CustomLikeRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public int updateLikeStatus(Like likes, int likeStatus){
        jpaQueryFactory
                .update(like)
                .set(like.likeStatus, likeStatus)
                .execute();
        return likeStatus;
    }
}
