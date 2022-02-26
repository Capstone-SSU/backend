package com.example.demo.service;

import com.example.demo.domain.Hashtag;
import com.example.demo.domain.Lecture;
import com.example.demo.domain.Review;
import com.example.demo.domain.ReviewHashtag;
import com.example.demo.dto.HashtagDto;
import com.example.demo.dto.LectureOnlyDto;
import com.example.demo.dto.LectureResponse;
import com.example.demo.dto.ReviewOnlyDto;
import com.example.demo.repository.HashtagRepository;
import com.example.demo.repository.LectureRepository;
import com.example.demo.repository.ReviewHashtagRepository;
import com.example.demo.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureService {
    private final LectureRepository lectureRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewHashtagRepository reviewHashtagRepository;
    private final HashtagRepository hashtagRepository;

    public long saveLecture(Lecture lecture){
        Lecture savedLecture = lectureRepository.save(lecture);
        return savedLecture.getLectureId();
    }

    // 전체 강의 조회
    public List<Lecture> findAllLectures (){
        List<Lecture> lectures = lectureRepository.findAll();
        System.out.println("lectures.size() = " + lectures.size());
        return lectures!=null?lectures:Collections.emptyList();
    }

    // 특정 강의 조회
    public LectureResponse findById(Long lectureId){
        LectureResponse lectureResponse = new LectureResponse();
        Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId); // lecture 데이터 가져와서
        if(optionalLecture.isEmpty())
            return lectureResponse;
        Lecture lecture = optionalLecture.get();
//        LectureOnlyDto lectureOnlyDto = new LectureOnlyDto(); // 원본객체 복사할 때 사용
//        BeanUtils.copyProperties(lecture, lectureOnlyDto,"reviews", "user"); // 원본 객체, 복사 대상 객체
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
        List<ReviewOnlyDto> reviewOnlyDtos = new ArrayList<>();
        Map<Long, Integer> hashtagCnt = new HashMap<>(); // 해시태그 상위 3개 찾기 위해서
        for(int i=0;i<reviews.size();i++){ // 특정 강의에 해당하는 리뷰들을 찾기 위해서
            ReviewOnlyDto reviewOnlyDto = new ReviewOnlyDto();
            BeanUtils.copyProperties(reviews.get(i), reviewOnlyDto,"reviewHashtags"); // 원본 객체, 복사 대상 객체
            reviewOnlyDtos.add(reviewOnlyDto);

            totalRate += reviews.get(i).getRate();
            List<ReviewHashtag> reviewHashtags = reviewHashtagRepository.findByReview(reviews.get(i));

            for(int j=0;j<reviewHashtags.size();j++){
                long hashtagId = reviewHashtags.get(j).getHashtag().getHashtagId();
                // 이미 키 값이 존재하면 해당 value + 1
                if(hashtagCnt.containsKey(hashtagId)){
                    int cnt = hashtagCnt.get(hashtagId);
                    hashtagCnt.put(hashtagId, cnt+1);
                }
                else{ // 키가 존재하지 않는 경우
                    hashtagCnt.put(hashtagId, 1);
                }
            }
        }
        lectureResponse.setReviews(reviewOnlyDtos);

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
        lectureResponse.setHashtags(hashtags);
        lectureResponse.setAvgRate(totalRate/reviews.size()); // 평균 점수 계산


        return lectureResponse;
    }

    // url 중복 조회용
    public Lecture findByUrl(String lectureUrl){
        Optional<Lecture> lecture = lectureRepository.findBylectureUrl(lectureUrl);
        return lecture.orElse(null);
    }
}
