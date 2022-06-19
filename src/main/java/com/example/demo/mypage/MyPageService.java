package com.example.demo.mypage;

import com.example.demo.lecture.LectureService;
import com.example.demo.lecture.RequestedLecture;
import com.example.demo.lecture.repository.RequestedLectureRepository;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.mypage.dto.*;
import com.example.demo.review.Review;
import com.example.demo.review.repository.ReviewRepository;
import com.example.demo.roadmap.RoadMap;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.roadmap.repository.RoadmapGroupRepository;
import com.example.demo.roadmap.repository.RoadmapRepository;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.study.repository.StudyPostRepository;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final ImageService imageService;
    private final RequestedLectureRepository  requestedLectureRepository;

    // 회원정보 수정 페이지 조회
    public InfoResponse getProfile(User user){
        InfoResponse myInfoResponse = InfoResponse.from(user);
        return myInfoResponse;
    }

    // 다른 사용자의 마이페이지 조회
    public MyPageResponse getMyPage(User user){
        MyPageResponse myPageResponse = MyPageResponse.from(user);
        myPageResponse.setLikedLectures(this.getLikedLectures(user));
        myPageResponse.setLikedStudies(this.getLikedStudies(user));
        myPageResponse.setLikedRoadmaps(this.getLikedRoadmaps(user));
        myPageResponse.setMyReviews(this.getMyReviews(user));
        myPageResponse.setMyStudies(this.getMyStudies(user));
        myPageResponse.setMyRoadmaps(this.getMyRoadmaps(user));
        return myPageResponse;
    }

    // 마이페이지 요청한 유저가 자신인지, 다른 사람인지
    public boolean checkLoginUser(User loginUser, User requestedUser){
        if (loginUser == requestedUser) // 자신의 마이페이지를 요청한 경우
            return true;
        return false;
    }

    // 비밀번호 존재여부 확인
    public boolean checkPasswordInput(MyInfoEditDto myInfoEditDto){
        String password = myInfoEditDto.getPassword();
        String newPassword = myInfoEditDto.getNewPassword();
        String confirmPassword = myInfoEditDto.getConfirmPassword();
        if(password!=null && newPassword!=null && confirmPassword!=null)
            return true;
        return false;
    }

    // 비밀번호 재설정 확인
    public String checkPassword(MyInfoEditDto myInfoEditDto, User user) {
        String password = myInfoEditDto.getPassword();
        String newPassword = myInfoEditDto.getNewPassword();
        String confirmPassword = myInfoEditDto.getConfirmPassword();

        // 현재 디비에 있는 비밀번호와 비교한 후
        if(bCryptPasswordEncoder.matches(password, user.getUserPassword())) {
            if (newPassword.equals(confirmPassword)) { // 비밀번호 확인 과정 거친 후 비번 업뎃
                String hashPassword = bCryptPasswordEncoder.encode(confirmPassword);
                user.updatePassword(hashPassword);
                return "success";
            } else return "not equals";
        }
        return "not match";
    }

    // 회원정보 수정
    public void updateProfile(MyInfoEditDto myInfoEditDto, User user) throws FileUploadException {
        String nickname = (myInfoEditDto.getUserNickname() == null) ?
                user.getUserNickname() :
                myInfoEditDto.getUserNickname();

        String githubUrlName = myInfoEditDto.getGithubUrlName() == null ?
                user.getGithubUrlName() :
                myInfoEditDto.getGithubUrlName();

        user.updateProfileName(nickname, githubUrlName);
        this.updateProfileImage(myInfoEditDto, user);
    }

    public void updateProfileImage(MyInfoEditDto myInfoEditDto, User user) throws FileUploadException {
        MultipartFile imageUrl = myInfoEditDto.getUserProfileImg();
        String url = (imageUrl == null) ?
                user.getUserProfileImg() :
                imageService.uploadFile(imageUrl);
        user.updateProfileImage(url);
    }

    // 좋아요한 강의
    public List<LikedLecturesResponse> getLikedLectures(User user) {
        List<LikedLecturesResponse> likedLectures = likeRepository
                .findLectureLikeByUser(user)
                .stream()
                .map(LikedLecturesResponse::from)
                .collect(Collectors.toList());

        likedLectures.forEach(lecture ->
                lecture.setHashtags(lectureService.getHashtags(lecture.getLectureId()))
        );
        return likedLectures;
    }

    // 좋아요한 스터디
    public List<LikedStudiesResponse> getLikedStudies(User user){
        List<LikedStudiesResponse> likedStudies = new ArrayList<>();
        List<StudyPost> studies = likeRepository.findStudyLikeByUser(user);
        for(int i=0;i<studies.size();i++){
            LikedStudiesResponse likedStudiesResponse = LikedStudiesResponse.from(studies.get(i));
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
            likedRoadmapsResponse.setRoadmapWriterNickname(group.getUser().getUserNickname());
            likedRoadmapsResponse.setRoadmapCreatedDate(group.getRoadmapGroupCreatedDate());
            likedRoadmaps.add(likedRoadmapsResponse);
        }
        return likedRoadmaps;
    }

    // 작성한 강의리뷰 조회
    public List<MyReviewsResponse> getMyReviews(User user){
        List<Review> reviews = reviewRepository.findByUser(user);

        return reviews
                .stream()
                .map(review -> MyReviewsResponse.from(review, review.getLecture()))
                .collect(Collectors.toList());
    }

    // 작성한 스터디 조회
    public List<MyStudiesResponse> getMyStudies(User user){
        List<MyStudiesResponse> myStudies = studyPostRepository.findByUser(user);
        myStudies.forEach(s->s.setProfileImage(user.getUserProfileImg()));
        myStudies.forEach(s->s.setWriterNickname(user.getUserNickname()));
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

    public List<RequestedLectureResponse> getMyRequestedLectures(User user){
        //url 로 강의 찾아서 lectureId 반환
        List<RequestedLecture> allByUser = requestedLectureRepository.findAllByUser(user);
        return allByUser.stream()
                .map(r->RequestedLectureResponse.fromEntity(r,lectureService.findByUrl(r.getLectureUrl())))
                .collect(Collectors.toList());
    }
}
