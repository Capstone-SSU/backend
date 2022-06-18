package com.example.demo.review;

import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.review.dto.ReviewDto;
import com.example.demo.review.dto.ReviewPostDto;
import com.example.demo.review.repository.ReviewRepository;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public void saveReview(ReviewDto reviewDto, User user, Lecture lecture){
        // 처음 리뷰 쓰는 경우 status 변경
        if(reviewRepository.findByUser(user).isEmpty())
            user.updateReviewWriteStatus();

        Review review = reviewDto.toEntity(user, lecture);
        reviewRepository.save(review);
        this.updateAvgRate(lecture);
    }

    // 평점 계산 (리뷰를 등록, 수정,삭제할 때마다 계산을 다시해서 SETTING 해야 함)
    public void updateAvgRate(Lecture lecture){
        List<Review> reviews = reviewRepository.findByLecture(lecture);

        double rateSum = reviews.stream().mapToDouble(i -> i.getRate()).sum();
        double avgRate = Math.round((rateSum/reviews.size()*10))/10.0;
        lecture.updateAvgRate(avgRate);
    }

    public Review findByReviewId(Long reviewId){
        // 삭제된 것은 빼고 조회하기
        Optional<Review> review = reviewRepository
                .findById(reviewId)
                .filter(oneReview -> oneReview.getReviewStatus()!=0);
        return review.orElse(null);
    }

    public Review findByUserAndLecture(User user, Lecture lecture){ // fk 로 접근할 때 객체로 넘기자
        return reviewRepository
                .findByUserAndLecture(user, lecture)
                .orElse(null);
    }

    public void deleteReviews(Lecture lecture){
        reviewRepository.deleteReviews(lecture);
        this.updateAvgRate(lecture);
    }

    public void updateReview(ReviewPostDto reviewUpdateDto, Review review){
        reviewRepository.updateReview(reviewUpdateDto, review.getReviewId());
        this.updateAvgRate(review.getLecture());
    }

    public void deleteReview(Long reviewId, User user){
        reviewRepository.deleteReview(reviewId);

        // 이걸 삭제했을 때 리뷰가 하나도 없다면 reviewWriteStatus 바꾸기
        if(reviewRepository.findByUser(user).isEmpty())
            user.updateReviewWriteStatus();

        Review review = this.findByReviewId(reviewId);
        this.updateAvgRate(review.getLecture());
    }

    public List<Review> findAllReviewsByUser(User user){
        List<Review> reviews = reviewRepository.findByUser(user);
        reviews.removeIf(review -> review.getReviewStatus() == 0);
        return reviews;
    }
}

