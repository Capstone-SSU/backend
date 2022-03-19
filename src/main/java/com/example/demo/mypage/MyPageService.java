package com.example.demo.mypage;
import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.lecture.dto.DetailLectureResponse;
import com.example.demo.mypage.dto.*;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.review.Review;
import com.example.demo.review.repository.ReviewRepository;
import com.example.demo.roadmap.RoadMap;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.roadmap.repository.RoadmapGroupRepository;
import com.example.demo.roadmap.repository.RoadmapRepository;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.study.repository.StudyPostRepository;
import com.example.demo.user.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;
    private final LectureService lectureService;
    private final StudyPostRepository studyPostRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapGroupRepository roadmapGroupRepository;
    private final UserRepository userRepository;

    // 회원정보 수정 페이지 조회
    public InfoResponse getProfile(User user){
        String email = user.getUserEmail();
        String nickname = user.getUserNickname();
        String profileImage = user.getUserProfileImg();
        String githubName = user.getGithubUrlName();
        InfoResponse myInfoResponse = new InfoResponse(email, nickname, profileImage, githubName);
        return myInfoResponse;
    }

    // 다른 사용자의 마이페이지 조회
    public MyPageResponse getMyPage(User user){
        MyPageResponse myPageResponse = new MyPageResponse();
        myPageResponse.setUserNickname(user.getUserNickname());
        myPageResponse.setUserProfileImg(user.getUserProfileImg());
        myPageResponse.setGithubUrlName(user.getGithubUrlName());
        myPageResponse.setUserCompany(user.getUserCompany());
        myPageResponse.setLikedLectures(this.getLikedLectures(user));
        myPageResponse.setLikedStudies(this.getLikedStudies(user));
        myPageResponse.setLikedRoadmaps(this.getLikedRoadmaps(user));
        myPageResponse.setMyReviews(this.getMyReviews(user));
        myPageResponse.setMyStudies(this.getMyStudies(user));
        myPageResponse.setMyRoadmaps(this.getMyRoadmaps(user));
        return myPageResponse;
    }

    // 회원정보 수정
    public void editProfile(MyInfoEditDto myInfoEditDto, User user, String url){
        String nickname = myInfoEditDto.getUserNickname();
        String githubUrlName = myInfoEditDto.getGithubUrlName();
        user.updateProfile(nickname, url, githubUrlName);
    }

    public String checkPassword(User user, String password, String newPassword, String confirmPassword) {
        // 현재 디비에 있는 비밀번호와 비교한 후
        if(bCryptPasswordEncoder.matches(password, user.getUserPassword())){
            if(newPassword.equals(confirmPassword)) { // 비밀번호 확인 과정 거친 후 비번 업뎃
                String hashPassword = bCryptPasswordEncoder.encode(confirmPassword);
                user.updatePassword(hashPassword);
                user.setUserPassword(hashPassword);
                return "success";
            }
            else return "not equals";
        }
        return "not match";
    }


    // 좋아요한 강의
    public List<LikedLecturesResponse> getLikedLectures(User user){
        List<Lecture> lectures = likeRepository.findLectureLikeByUser(user);
        List<LikedLecturesResponse> likedLectures = new ArrayList<>();
        for(int i=0;i<lectures.size();i++){
            Lecture lecture = lectures.get(i);
            DetailLectureResponse detailLectureResponse = lectureService.getLecture(lecture.getLectureId(), user.getUserId());
            LikedLecturesResponse likedLecturesResponse = new LikedLecturesResponse();
            BeanUtils.copyProperties(detailLectureResponse, likedLecturesResponse,"reviewCnt", "likeCnt", "reviews"); // 원본 객체, 복사 대상 객체
            likedLectures.add(likedLecturesResponse);
        }
        return likedLectures;
    }

    // 좋아요한 스터디
    public List<LikedStudiesResponse> getLikedStudies(User user){
        List<StudyPost> studies = likeRepository.findStudyLikeByUser(user);
        List<LikedStudiesResponse> likedStudies = new ArrayList<>();
        for(int i=0;i<studies.size();i++){
            StudyPost studyPost = studies.get(i);
            long studyPostId = studyPost.getStudyPostId();
            String studyTitle = studyPost.getStudyTitle();
            LocalDateTime studyCreatedDate = studyPost.getStudyCreatedDate();
            String studyLocation = studyPost.getStudyLocation();
            String studyRecruitState = (studyPost.getStudyRecruitStatus()==1) ? "모집중" : "모집완료";
            String studyCategoryName = studyPost.getStudyCategoryName();
            String nickname = user.getUserNickname();
            String profileImage = user.getUserProfileImg();
            LikedStudiesResponse likedStudiesResponse = new LikedStudiesResponse(studyPostId, studyTitle, studyCreatedDate, studyLocation, studyRecruitState, studyCategoryName, nickname, profileImage);
            likedStudies.add(likedStudiesResponse);
        }
        return likedStudies;
    }

    // 좋아요한 로드맵 조회
    public List<LikedRoadmapsResponse> getLikedRoadmaps(User user){
        List<LikedRoadmapsResponse> likedRoadmaps = new ArrayList<>();
        List<RoadMapGroup> allRoadmapGroups = likeRepository.findRoadmapLikeByUser(user);
        for(RoadMapGroup group:allRoadmapGroups){ // 로드맵을 하나씩 돌면서
            LikedRoadmapsResponse likedRoadmapsResponse = new LikedRoadmapsResponse();
            List<RoadMap> roadmaps = roadmapRepository.findAllRoadmapsByGroup(group); // 로드맵에 들어있는 각각의 강의들
            List<String> thumbnails=new ArrayList<>();
            for(RoadMap roadMap: roadmaps){
                thumbnails.add(roadMap.getLecture().getThumbnailUrl());
            }
            likedRoadmapsResponse.setRoadmapId(group.getRoadmapGroupId());
            likedRoadmapsResponse.setRoadmapTitle(group.getRoadmapGroupTitle());
            likedRoadmapsResponse.setRoadmapWriterCompany(user.getUserCompany());
            likedRoadmapsResponse.setLectureThumbnails(thumbnails);
            likedRoadmapsResponse.setRoadmapWriterNickname(user.getUserNickname());
            likedRoadmapsResponse.setRoadmapCreatedDate(group.getRoadmapGroupCreatedDate());
            likedRoadmaps.add(likedRoadmapsResponse);
        }
        return likedRoadmaps;
    }

    // 작성한 강의리뷰 조회
    public List<MyReviewsResponse> getMyReviews(User user){
        List<Review> reviews = reviewRepository.findByUser(user);
        List<MyReviewsResponse> myReviews = new ArrayList<>();
        for(int i=0;i<reviews.size();i++){
            Review review = reviews.get(i);
            Lecture lecture = review.getLecture();
            long lectureId = lecture.getLectureId();
            String thumbnailUrl = lecture.getThumbnailUrl();
            String lectureTitle = lecture.getLectureTitle();
            String commentTitle = review.getCommentTitle();
            String comment = review.getComment();
            double avgRate = lectureService.getAvgRate(lecture);
            MyReviewsResponse myReviewsResponse = new MyReviewsResponse(lectureId, thumbnailUrl, lectureTitle, avgRate, commentTitle, comment);
            myReviews.add(myReviewsResponse);
        }
        return myReviews;
    }

    // 작성한 스터디 조회
    public List<MyStudiesResponse> getMyStudies(User user){
        List<MyStudiesResponse> myStudies = studyPostRepository.findByUser(user);
        return myStudies;
    }

    // 작성한 로드맵 조회
    public List<MyRoadmapsResponse> getMyRoadmaps(User user){
        // 자신이 쓴 로드맵 그룹만 가져오기
        List<MyRoadmapsResponse> myRoadmaps = new ArrayList<>();
        List<RoadMapGroup> allRoadmapGroups = roadmapGroupRepository.findAllRoadmapsByUser(user);
        for(RoadMapGroup group:allRoadmapGroups){ // 로드맵을 하나씩 돌면서
            MyRoadmapsResponse myRoadmapsResponse = new MyRoadmapsResponse();

            List<RoadMap> roadmaps = roadmapRepository.findAllRoadmapsByGroup(group); // 로드맵에 들어있는 각각의 강의들
            List<String> thumbnails=new ArrayList<>();
            for(RoadMap roadMap: roadmaps){
                thumbnails.add(roadMap.getLecture().getThumbnailUrl());
            }
            myRoadmapsResponse.setRoadmapId(group.getRoadmapGroupId());
            myRoadmapsResponse.setRoadmapTitle(group.getRoadmapGroupTitle());
            myRoadmapsResponse.setRoadmapWriterCompany(user.getUserCompany());
            myRoadmapsResponse.setLectureThumbnails(thumbnails);
            myRoadmaps.add(myRoadmapsResponse);
        }
        return myRoadmaps;
    }
}
