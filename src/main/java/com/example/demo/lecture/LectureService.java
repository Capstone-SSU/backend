package com.example.demo.lecture;

import com.example.demo.hashtag.repository.HashtagRepository;
import com.example.demo.lecture.dto.*;
import com.example.demo.lecture.repository.LectureSpecification;
import com.example.demo.lecture.repository.RequestedLectureRepository;
import com.example.demo.lectureHashtag.LectureHashtag;
import com.example.demo.like.Like;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.review.dto.DetailReviewResponse;
import com.example.demo.hashtag.Hashtag;
import com.example.demo.lecture.repository.LectureRepository;
import com.example.demo.review.Review;
import com.example.demo.review.dto.ReviewPostDto;
import com.example.demo.review.repository.ReviewRepository;
import com.example.demo.lectureHashtag.LectureHashtagRepository;
import com.example.demo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureService {
    private final RequestedLectureRepository requestedLectureRepository;
    private final LectureRepository lectureRepository;
    private final ReviewRepository reviewRepository;
    private final LectureHashtagRepository lectureHashtagRepository;
    private final HashtagRepository hashtagRepository;
    private final LikeRepository likeRepository;

    // 추천용 강의 데이터 가공 함수
    public List<RecLecturesResponse> manageRecommendData(Pageable pageable) {
        /*
            ’강의 번호’,
            ’강의 제목’,
            ’평점’,
            ’리뷰를 한 사용자의 수’,
            ’키워드(해시)’
        */
        List<RecLecturesResponse> recLectures = new ArrayList<>();
        Page<AllLecturesResponse> lectures = this.getLectures(pageable); // 전체글에서 필터링해보기
        for (int i = 0; i < lectures.getContent().size(); i++) {
            Long lectureId = lectures.getContent().get(i).getLectureId();
            Lecture lecture = findById(lectureId);
            RecLecturesResponse recLecturesResponse = RecLecturesResponse.from(lecture);
            recLecturesResponse.setHashtags(this.getHashtags(lecture));
            recLectures.add(recLecturesResponse);
        }
        return recLectures;
    }

    // 전체 강의 조회
    public Page<AllLecturesResponse> getLectures(Pageable pageable) {
        return lectureRepository.findAll(pageable).map(AllLecturesResponse::from);
    }

    // 검색어별 조회
    public List<AllLecturesResponse> getFilteredLectures(Pageable pageable, String keyword, String category) {
        List<AllLecturesResponse> lectures = new ArrayList<>();
        if (keyword != null) { // 검색어(키워드)만 있는 경우
            String[] keywords = keyword.split(" "); // 검색어(키워드)에 공백있는 경우
            for (int i = 0; i < keywords.length; i++) {
                String word = keywords[i];
                lectures.addAll(lectureRepository.findAll(LectureSpecification.titleLike(word), pageable).map(AllLecturesResponse::from).getContent());
            }
        }
        if(category!=null){ // 카테고리(해시태그)만 있는 경우
            List<String> categories = Arrays.asList(category.split(",")); // 카테고리 받아온거
            lectures.addAll(this.getLectures(pageable).getContent());
            for(int i=0;i<lectures.size();i++) { // 강의 전체를 돌면서
                Lecture lecture = this.findById(lectures.get(i).getLectureId());
                List<String> hashtags = this.getHashtags(lecture);
                List<String> finalList = hashtags.stream()
                        .filter(element -> listContains(categories, element)) // 사용자가 원하는 카테고리에 해당 강의의 hashtag 중 하나라도 포함되어 있는 경우
                        .collect(Collectors.toList());
                if(finalList.isEmpty()) { // 포함되는게 없는 것은 빼기
                    lectures.remove(i--); // remove 할 때 인덱스도 같이 줄여줌
                }
            }
        }
        return lectures;
    }

    // 검색어별 조회
//    public Page<AllLecturesResponse> getFilteredLectures(Pageable pageable,String keyword, String category){
//        Page<AllLecturesResponse> lectures = this.getLectures(pageable); // 전체글에서 필터링해보기
//        if(keyword!=null){ // 키워드만 있는 경우
//            String[] keywords = keyword.split(" ");
//            for(int i=0;i<keywords.length;i++){
//                String word = keywords[i];
//                lectures.getContent().stream() // 제목에 키워드 포함된 거 가져오기
//                        .filter(lecture -> lecture.getLectureTitle().contains(word))
//                        .collect(Collectors.toList());
//            }
//        }
//
//        if(category!=null){ // 카테고리(해시태그)만 있는 경우
//            List<String> categories = Arrays.asList(category.split(",")); // 카테고리 받아온거
//            for(int i=0;i<lectures.getContent().size();i++) { // 강의 전체를 돌면서
//                Lecture lecture = this.findById(lectures.getContent().get(i).getLectureId());
//                List<String> hashtags = this.getHashtags(lecture);
//                List<String> finalList = hashtags.stream()
//                        .filter(element -> listContains(categories, element)) // 사용자가 원하는 카테고리에 해당 강의의 hashtag 중 하나라도 포함되어 있는 경우
//                        .collect(Collectors.toList());
//                if(finalList.isEmpty()) { // 포함되는게 없는 것은 빼기
//                    lectures.getContent().remove(i--); // remove 할 때 인덱스도 같이 줄여줌
//                }
//            }
//        }
//        return lectures;
//    }

    public static <T> boolean listContains(List<T> array, T element) { // categories / hashtag 중 하나
        // (1,2,3) in (3,4,5) -> 3 출력
        return array.stream()
                .filter(e -> e.equals(element)) // categories 의 category에서 hashtag랑 같은거
                .findFirst().isPresent();
    }

    // 특정 강의 조회
    public Lecture findById(Long lectureId){
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        return lecture.orElse(null);
    }

    // 강의글 상세 조회
    public DetailLectureResponse getLecture(Lecture lecture, User user){
        DetailLectureResponse detailLectureResponse = DetailLectureResponse.from(lecture);
        List<Review> reviews = reviewRepository.findByLecture(lecture); // lecture 를 갖고 reviews 에 있는 모든 데이터 가져오기
        List<DetailReviewResponse> detailReviewResponses = new ArrayList<>();
        for(int i=0;i<reviews.size();i++){ // 특정 강의에 해당하는 리뷰들을 찾기 위해서
            DetailReviewResponse detailReviewResponse = DetailReviewResponse.from(reviews.get(i), lecture); // 해당 리뷰글 내가 쓴건지 니가 쓴건지 구분해야함
            if(user.getUserId() == reviews.get(i).getUser().getUserId()) // 리뷰 등록자와 로그인한 사용자가 같다면
                detailReviewResponse.setWriterStatus(true);
            detailReviewResponses.add(detailReviewResponse);
        }
        detailLectureResponse.setReviews(detailReviewResponses);
        detailLectureResponse.setHashtags(this.getHashtags(lecture));

        // 좋아요 누른 여부
        Optional<Like> like = likeRepository.findLikeByLectureAndUser(lecture, user);
        if(like.isPresent())
            detailLectureResponse.setLikeStatus(true);
        else
            detailLectureResponse.setLikeStatus(false);
        return detailLectureResponse;
    }

    // 강의 등록
    public long saveLecture(Lecture lecture){
        Lecture savedLecture = lectureRepository.save(lecture);
        return savedLecture.getLectureId();
    }

    // 강의 수정
//    public void updateLecture(LectureDto lectureDto, Long lectureId){
//        lectureRepository.updateLecture(lectureDto, lectureId);
//    }
//
//    // 강의 삭제
//    public void deleteLecture(Long lectureId){
//        lectureRepository.deleteLecture(lectureId);
//    }


    // 강의 요청 url 등록
    public void saveRequestedLecture(String url){
        RequestedLecture requestedLecture = new RequestedLecture();
        requestedLecture.setLectureUrl(url);
        requestedLectureRepository.save(requestedLecture);
    }

    // 강의 요청된 url 확인
    public RequestedLecture findRequestedLecture(String url){
        Optional<RequestedLecture> requestedLecture = requestedLectureRepository.findByLectureUrl(url);
        return requestedLecture.orElse(null);
    }

    // 해시태그 저장
    public void manageHashtag(List<String> hashtags, Lecture lecture){
        for (int i = 0; i < hashtags.size(); i++) {
            Optional<Hashtag> existedHashtag = hashtagRepository.findByHashtagName(hashtags.get(i));
            LectureHashtag lectureHashtag = new LectureHashtag();
            if(existedHashtag.isPresent()) { // 이미 들어간 해시태그라면 id 받아오기
                lectureHashtag.setHashtag(existedHashtag.get());
            }
            else { // 없는 해시태그라면 해시태그를 생성하고 나서 lectureHashtag에 넣기
                Hashtag hashtag = new Hashtag(hashtags.get(i));
                hashtagRepository.save(hashtag);
                lectureHashtag.setHashtag(hashtag);
            }
            lectureHashtag.setLecture(lecture);
            lectureHashtagRepository.save(lectureHashtag);
        }
    }

    // 특정 Lecture에 해당하는 해시태그 상위 3개 가져오는 함수
//    public List<String> getBestHashtags(Lecture lecture){
//        List<Review> reviews = reviewRepository.findByLecture(lecture); // lecture 를 갖고 reviews 에 있는 모든 데이터 가져오기
//        Map<Long, Integer> hashtagCnt = new HashMap<>(); // 해시태그 상위 3개 찾기 위해서
//        for(int i=0;i<reviews.size();i++){ // 특정 강의에 해당하는 리뷰들을 돌면서 해시태그 개수 세기
//            List<LectureHashtag> reviewHashtags = lectureHashtagRepository.findByReview(reviews.get(i));
//
//            for(int j=0;j<reviewHashtags.size();j++){
//                long hashtagId = reviewHashtags.get(j).getHashtag().getHashtagId();
//                if(hashtagCnt.containsKey(hashtagId)){                 // 이미 키 값이 존재하면 해당 value + 1
//                    int cnt = hashtagCnt.get(hashtagId);
//                    hashtagCnt.put(hashtagId, cnt+1);
//                }
//                else{ // 키가 존재하지 않는 경우
//                    hashtagCnt.put(hashtagId, 1);
//                }
//            }
//        }
//
//        // hashmap 내림차순 정렬 후 3개까지만 자르기
//        List<Map.Entry<Long, Integer>> entryList = new LinkedList<>(hashtagCnt.entrySet());
//        entryList.sort((o1, o2) -> hashtagCnt.get(o1.getKey()) - hashtagCnt.get(o2.getKey()));
//        int limit = 0;
//        List<String> hashtags = new ArrayList<>(); // hashtag 담을 list 생성
//        for(Map.Entry<Long, Integer> entry : entryList){
//            if(limit == 3)
//                break;
//            Optional<Hashtag> hashtag = hashtagRepository.findById(entry.getKey());
//            String hashtagName = hashtag.get().getHashtagName();
//            hashtags.add(hashtagName);
//            limit++;
//        }
//        return hashtags;
//    }

    // 평균 평점 업데이트
    public void setAvgRate(Lecture lecture, int rate){
        List<Review> reviews = reviewRepository.findByLecture(lecture); // lecture 를 갖고 reviews 에 있는 모든 데이터 가져오기
        lecture.setAvgRate(Math.round((lecture.getAvgRate()+rate)/reviews.size()*10)/10.0);
    }

    // 강의 해시태그 가져오기
    public List<String> getHashtags(Lecture hashtagLecture){
        Optional<Lecture> lecture = lectureRepository.findById(hashtagLecture.getLectureId());
        List<String> hashtags = new ArrayList<>(); // hashtag 담을 list 생성
        List<LectureHashtag> lectureHashtags = lectureHashtagRepository.findByLecture(lecture.get());
        for(int i=0;i<lectureHashtags.size();i++)
            hashtags.add(lectureHashtags.get(i).getHashtag().getHashtagName());
        return hashtags;
    }

    // url 중복 조회용
    public Lecture findByUrl(String lectureUrl){
        Optional<Lecture> lecture = lectureRepository.findBylectureUrl(lectureUrl);
        return lecture.orElse(null);
    }

    // url 중복 조회 후 있으면 리턴
    public LectureUrlResponse getLectureUrl(String lectureUrl){
        Optional<Lecture> lecture = Optional.ofNullable(this.findByUrl(lectureUrl));
        if(lecture.isPresent()) {
            LectureUrlResponse lectureUrlResponse = LectureUrlResponse.from(lecture.get());
            lectureUrlResponse.setHashtags(this.getHashtags(lecture.get()));
            return lectureUrlResponse;
        }
        else return null;
    }
}