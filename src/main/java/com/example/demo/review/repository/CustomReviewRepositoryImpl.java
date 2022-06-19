package com.example.demo.review.repository;
import com.example.demo.lecture.Lecture;
import com.example.demo.review.Review;
import com.example.demo.review.dto.ReviewPostDto;
import com.example.demo.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import static com.example.demo.review.QReview.review;

@Repository
@RequiredArgsConstructor
@Transactional
public class CustomReviewRepositoryImpl implements CustomReviewRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Review> findByUser(User user) {
        return jpaQueryFactory
                .selectFrom(review)
                .where(review.reviewStatus.eq(1))
                .where(review.user.eq(user))
                .fetch();
    }

    @Override
    public void updateReview(ReviewPostDto reviewUpdateDto, Long reviewId){
        jpaQueryFactory
                .update(review)
                .set(review.commentTitle, reviewUpdateDto.getCommentTitle())
                .set(review.comment, reviewUpdateDto.getComment())
                .set(review.rate, reviewUpdateDto.getRate())
                .where(review.reviewId.eq(reviewId))
                .execute();
    }

    @Override
    public void deleteReview(Long reviewId){
        jpaQueryFactory
                .update(review)
                .set(review.reviewStatus, 0)
                .where(review.reviewId.eq(reviewId))
                .execute();
    }

    @Override
    public void deleteReviews(Lecture lecture) {
        jpaQueryFactory
                .update(review)
                .set(review.reviewStatus, 0)
                .where(review.lecture.eq(lecture))
                .execute();
    }

    @Override
    public List<Review> findByLecture(Lecture lecture) {
        return jpaQueryFactory
                .selectFrom(review)
                .where(review.reviewStatus.eq(1))
                .where(review.lecture.eq(lecture))
                .fetch();
    }

    @Override
    public Optional<Review> findByUserAndLecture(User user, Lecture lecture) {
        return jpaQueryFactory
                .selectFrom(review)
                .where(review.reviewStatus.eq(1))
                .where(review.lecture.eq(lecture))
                .where(review.user.eq(user))
                .stream().findFirst();
    }
}
