package com.example.demo.lecture;

import com.example.demo.hashtag.repository.HashtagRepository;
import com.example.demo.lecture.dto.LectureResponse;
import com.example.demo.like.Like;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.reviewHashtag.ReviewHashtag;
import com.example.demo.review.dto.DetailReviewResponse;
import com.example.demo.hashtag.Hashtag;
import com.example.demo.lecture.repository.LectureRepository;
import com.example.demo.review.Review;
import com.example.demo.review.repository.ReviewRepository;
import com.example.demo.reviewHashtag.ReviewHashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureService {
    private final LectureRepository lectureRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewHashtagRepository reviewHashtagRepository;
    private final HashtagRepository hashtagRepository;
    private final LikeRepository likeRepository;

    public long saveLecture(Lecture lecture){
        Lecture savedLecture = lectureRepository.save(lecture);
        return savedLecture.getLectureId();
    }

    // 전체 강의 조회
    public List<Lecture> getAllLectures (){
        List<Lecture> lectures = lectureRepository.findAll();
        return lectures!=null?lectures:Collections.emptyList();
    }

    public Lecture findById(long lectureId){
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        return lecture.orElse(null);
    }

    public void manageHashtag(List<String> hashtags, Review review){
        for (int i = 0; i < hashtags.size(); i++) {
            Hashtag existedHashtag = hashtagRepository.findByHashtagName((hashtags.get(i))).get();
            ReviewHashtag reviewHashtag = new ReviewHashtag();
            if(existedHashtag!=null) { // 이미 들어간 해시태그라면 id 받아오기
                reviewHashtag.setHashtag(existedHashtag);
            }
            else { // 없는 해시태그라면 해시태그를 생성하고 나서 reviewHashtag 에 넣기
                Hashtag hashtag = new Hashtag(hashtags.get(i));
                hashtagRepository.save(hashtag);
                reviewHashtag.setHashtag(hashtag);
            }
            reviewHashtag.setReview(review);
            reviewHashtagRepository.save(reviewHashtag);
        }
    }

    // 특정 Lecture에 해당하는 해시태그 상위 3개 가져오는 함수
    public List<String> getBestHashtags(Lecture lecture){
        List<Review> reviews = reviewRepository.findByLecture(lecture); // lecture 를 갖고 reviews 에 있는 모든 데이터 가져오기
        Map<Long, Integer> hashtagCnt = new HashMap<>(); // 해시태그 상위 3개 찾기 위해서
        for(int i=0;i<reviews.size();i++){ // 특정 강의에 해당하는 리뷰들을 찾기 위해서
            List<ReviewHashtag> reviewHashtags = reviewHashtagRepository.findByReview(reviews.get(i));

            for(int j=0;j<reviewHashtags.size();j++){
                long hashtagId = reviewHashtags.get(j).getHashtag().getHashtagId();
                if(hashtagCnt.containsKey(hashtagId)){                 // 이미 키 값이 존재하면 해당 value + 1
                    int cnt = hashtagCnt.get(hashtagId);
                    hashtagCnt.put(hashtagId, cnt+1);
                }
                else{ // 키가 존재하지 않는 경우
                    hashtagCnt.put(hashtagId, 1);
                }
            }
        }

        // hashmap 내림차순 정렬 후 3개까지만 자르기
        List<Map.Entry<Long, Integer>> entryList = new LinkedList<>(hashtagCnt.entrySet());
        entryList.sort((o1, o2) -> hashtagCnt.get(o1.getKey()) - hashtagCnt.get(o2.getKey()));
        int limit = 0;
        List<String> hashtags = new ArrayList<>(); // hashtag 담을 list 생성
        for(Map.Entry<Long, Integer> entry : entryList){
            if(limit == 3)
                break;
            Optional<Hashtag> hashtag = hashtagRepository.findById(entry.getKey());
            String hashtagName = hashtag.get().getHashtagName();
            hashtags.add(hashtagName);
            limit++;
        }
        return hashtags;
    }


    // 특정 강의 조회
    public LectureResponse getLecture(long lectureId, long userId){
        LectureResponse lectureResponse = new LectureResponse();
        Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId); // lecture 데이터 가져와서
        if(optionalLecture.isEmpty())
            return lectureResponse;
        Lecture lecture = optionalLecture.get();
        lectureResponse.setLectureId(lecture.getLectureId());
        lectureResponse.setLectureTitle(lecture.getLectureTitle());
        lectureResponse.setLecturer(lecture.getLecturer());
        lectureResponse.setSiteName(lecture.getSiteName());
        lectureResponse.setLectureUrl(lecture.getLectureUrl());
        lectureResponse.setThumbnailUrl(lecture.getThumbnailUrl());

        List<Review> reviews = reviewRepository.findByLecture(lecture); // lecture 를 갖고 reviews 에 있는 모든 데이터 가져오기
        int reviewCnt = reviews.size();
        lectureResponse.setReviewCnt(reviewCnt); // 리뷰 개수 세팅

        int totalRate=0;
        List<DetailReviewResponse> detailReviewResponses = new ArrayList<>();
        for(int i=0;i<reviews.size();i++){ // 특정 강의에 해당하는 리뷰들을 찾기 위해서
            DetailReviewResponse detailReviewResponse = new DetailReviewResponse(); // 해당 리뷰글 내가 쓴건지 니가 쓴건지 구분해야함
            BeanUtils.copyProperties(reviews.get(i), detailReviewResponse,"reviewHashtags"); // 원본 객체, 복사 대상 객체
            String nickname = reviews.get(i).getUser().getUserNickname();
            detailReviewResponse.setNickname(nickname);

            if(userId == reviews.get(i).getUser().getUserId()) // 리뷰 등록자와 로그인한 사용자가 같다면
                detailReviewResponse.setWriterStatus(true);
            else
                detailReviewResponse.setWriterStatus(false);
            detailReviewResponses.add(detailReviewResponse);
            totalRate += reviews.get(i).getRate();
        }
        lectureResponse.setReviews(detailReviewResponses);
        lectureResponse.setHashtags(getBestHashtags(lecture)); // 특정 Lecture에 해당하는 해시태그 상위 3개 가져오는 함수 호출
        lectureResponse.setAvgRate(totalRate/reviews.size()); // 평균 점수 계산

        List<Like> likes = likeRepository.findLikeByLecture(lecture);
        lectureResponse.setLikeCnt(likes.size());
        return lectureResponse;
    }


    // url 중복 조회용
    public Lecture findByUrl(String lectureUrl){
        Optional<Lecture> lecture = lectureRepository.findBylectureUrl(lectureUrl);
        return lecture.orElse(null);
    }
}
