package com.example.demo.lecture;

import com.example.demo.hashtag.Hashtag;
import com.example.demo.hashtag.repository.HashtagRepository;
import com.example.demo.lecture.dto.AllLecturesResponse;
import com.example.demo.lecture.dto.DetailLectureResponse;
import com.example.demo.lecture.dto.LectureDto;
import com.example.demo.lecture.dto.LectureUrlResponse;
import com.example.demo.lecture.repository.LectureRepository;
import com.example.demo.lecture.repository.LectureSpecification;
import com.example.demo.lecture.repository.RequestedLectureRepository;
import com.example.demo.lectureHashtag.LectureHashtag;
import com.example.demo.lectureHashtag.LectureHashtagRepository;
import com.example.demo.like.Like;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.review.Review;
import com.example.demo.review.dto.DetailReviewResponse;
import com.example.demo.review.repository.ReviewRepository;
import com.example.demo.user.domain.User;
import com.example.demo.util.Crawler;
import com.sun.mail.iap.Response;
import lombok.RequiredArgsConstructor;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    private final Crawler crawler;

    // 전체 강의 조회 (페이지네이션)
    public Page<AllLecturesResponse> getLecturesByPage(Pageable pageable) {
        return lectureRepository.findAll(pageable).map(AllLecturesResponse::from);
    }

    // 전체 강의 조회
    public List<AllLecturesResponse> getLectures(){
        List<AllLecturesResponse> lectures = lectureRepository
                .findAll()
                .stream()
                .map(AllLecturesResponse::from)
                .collect(Collectors.toList());

        for(int i=0;i<lectures.size();i++){
            Lecture lecture = findById(lectures.get(i).getLectureId());
            lectures.get(i).setLikeCnt(this.getLikeCount(lecture));
        }
        return lectures;
    }

    // 검색어별 조회
    public List<AllLecturesResponse> getFilteredLectures(Pageable pageable, String keyword, String category) {
        List<AllLecturesResponse> lectures = new ArrayList<>(); // 반환하는 값
        List<Lecture> allLectures = lectureRepository.findAll(); // 전체 lecture list

        if (keyword != null) { // 검색어(키워드)만 있는 경우
            String[] keywords = keyword.split(" "); // 검색어(키워드)에 공백있는 경우
            for (int i = 0; i < keywords.length; i++) {
                String word = keywords[i];
                lectures.addAll(lectureRepository.findAll(LectureSpecification.titleLike(word), pageable).map(AllLecturesResponse::from).getContent());
            }
        }

        if(category!=null){ // 카테고리(해시태그)만 있는 경우
            List<String> categories = Arrays.asList(category.split(",")); // 카테고리 받아온거
//            lectureRepository.findByHashtag(categories);
            for(int i=0;i<allLectures.size();i++) { // 강의 전체를 돌면서
                Lecture lecture = this.findById(allLectures.get(i).getLectureId());
                List<String> hashtags = this.getHashtags(lecture.getLectureId()); // 해당강의의 해시태그 가져오기
                List<String> finalList = hashtags.stream()
                        .filter(element -> listContains(categories, element)) // 사용자가 원하는 카테고리에 해당 강의의 hashtag 중 하나라도 포함되어 있는 경우
                        .collect(Collectors.toList());
                if(!finalList.isEmpty()) { // 포함되는게 있는 것만 추가
                    lectures.add(AllLecturesResponse.from(lecture));
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

        detailLectureResponse.setLikeCnt(this.getLikeCount(lecture));
        detailLectureResponse.setReviewCnt(this.getReviewCount(lecture));
        detailLectureResponse.setReviews(detailReviewResponses);
        detailLectureResponse.setHashtags(this.getHashtags(lecture.getLectureId()));

        // 좋아요 누른 여부
        Optional<Like> like = likeRepository.findLikeByLectureAndUser(lecture, user);
        if(like.isPresent()) { // 좋아요 요청을 했던 경우
            if (like.get().getLikeStatus() == 1)
                detailLectureResponse.setLikeStatus(true);
            else
                detailLectureResponse.setLikeStatus(false);
        } else
            detailLectureResponse.setLikeStatus(false);
        return detailLectureResponse;
    }

    // 강의 등록
    public long saveLecture(Lecture lecture){
        Lecture savedLecture = lectureRepository.save(lecture);
        return savedLecture.getLectureId();
    }

    // 강의 수정
    public void updateLecture(LectureDto lectureDto, Long lectureId){
        lectureRepository.updateLecture(lectureDto, lectureId);
    }

    // 강의 삭제
    public void deleteLecture(Long lectureId){
        lectureRepository.deleteLecture(lectureId);
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

    // 강의에 달린 리뷰 갯수 가져오기
    public int getReviewCount(Lecture lecture){
        List<Review> reviews = reviewRepository.findByLecture(lecture); // lecture 를 갖고 reviews 에 있는 모든 데이터 가져오기
        return reviews.size();
    }

    // 강의 좋아요 갯수 가져오기
    public int getLikeCount(Lecture lecture){
        List<Like> likes = likeRepository.findLikeByLecture(lecture); // lecture 를 갖고 reviews 에 있는 모든 데이터 가져오기
        return likes.size();
    }


    // 강의 해시태그 가져오기
    public List<String> getHashtags(Long lectureId){
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
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
        Lecture lecture = this.findByUrl(lectureUrl);
        if(lecture != null) {
            LectureUrlResponse lectureUrlResponse = LectureUrlResponse.from(lecture);
            lectureUrlResponse.setHashtags(this.getHashtags(lecture.getLectureId()));
            return lectureUrlResponse;
        }
        else return null;
    }

    // 강의 요청된 url 확인
    public RequestedLecture findByRequestedLecture(String url){
        Optional<RequestedLecture> requestedLecture = requestedLectureRepository.findByLectureUrl(url);
        return requestedLecture.orElse(null);
    }

    // 강의 요청 url 등록
    public Long saveRequestedLecture(String url,User user){
        RequestedLecture lecture = RequestedLecture.builder()
                .url(url)
                .user(user)
                .build();
        return requestedLectureRepository.save(lecture).getRequestedLectureId();
    }


    public int callRequestedLectureCrawler(String url,Long lectureId){
        if(url.contains("nomadcoders")){
            crawler.nomadcoders(url,lectureId);
        }else if(url.contains("projectlion")){
            crawler.projectlion(url,lectureId);
        }else if(url.contains("udemy")){
            crawler.udemy(url,lectureId);
        }else if(url.contains("youtu")){
            crawler.youtube(url,lectureId);
        }else if(url.contains("fastcampus")){
            crawler.fastcampus(url,lectureId);
        }else if(url.contains("inflearn")){
            crawler.inflearn(url,lectureId);
        }else if(url.contains("spartacoding")){
            crawler.spartaCoding(url,lectureId);
        }else if(url.contains("opentutorials")){
            crawler.codingEverybody(url,lectureId);
        }else{
            return -1;
        }
        return 1;
    }
}