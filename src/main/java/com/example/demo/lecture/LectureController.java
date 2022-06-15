package com.example.demo.lecture;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.lecture.dto.*;
import com.example.demo.like.Like;
import com.example.demo.like.LikeService;
import com.example.demo.review.Review;
import com.example.demo.review.ReviewService;
import com.example.demo.review.dto.ReviewDto;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.user.domain.Role;
import com.example.demo.user.domain.User;
import com.example.demo.userPreferenceHashtag.UserPreferenceHashtagService;
import com.example.demo.util.Crawler;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@Api(tags={"강의리뷰 API"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/lectures")
public class LectureController {
    private final LectureService lectureService;
    private final ReviewService reviewService;
    private final UserDetailsServiceImpl userDetailsService;
    private final LikeService likeService;
    private final UserPreferenceHashtagService preferenceHashtagService;
    private final RecommendService recommendService;

    // 추천 알고리즘 전송용 메소드 - 전체 강의 데이터에 대해서
    @GetMapping("/recommend/all")
    public ResponseEntity<ResponseMessage> sendDataForRecommendation() {
        List<AllLecturesForRecommendResponse> recLectures = recommendService.manageAllData();
        return new ResponseEntity<>(ResponseMessage.withData(200, "추천 알고리즘용 모든 강의 데이터 전송 완료", recLectures), HttpStatus.OK);
    }

    // 추천 알고리즘 전송용 메소드 - 사용자가 좋아요한 강의 데이터에 대해서
    @GetMapping("/recommend/liked/{userId}")
    public ResponseEntity<ResponseMessage> sendLikedData(@PathVariable("userId") Long userId) {
        User user = userDetailsService.findUserById(userId);
        List<LikedLecturesForRecommendResponse> recLikedLectures = recommendService.manageLikedData(user);
        return new ResponseEntity<>(ResponseMessage.withData(200, "추천 알고리즘용 좋아요한 강의 데이터 강의 전송 완료", recLikedLectures), HttpStatus.OK);
    }

    // 관리자용 강의 등록
    @PostMapping("")
    public ResponseEntity<ResponseMessage> createLecture(Principal principal) throws IOException, InvalidFormatException {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        if(!user.getRole().equals(Role.ADMIN)) // 관리자 유저가 아닌경우
            return new ResponseEntity<>(new ResponseMessage(403, "관리자 권한이 아닌 유저입니다"), HttpStatus.FORBIDDEN);

        OPCPackage opcPackage = OPCPackage.open("C:\\Users\\Windows10\\Downloads\\캡스톤2 강의.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
        Sheet worksheet = workbook.getSheetAt(0);
        System.out.println(worksheet.getPhysicalNumberOfRows());
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 4
            Row row = worksheet.getRow(i);
            String lectureUrl = row.getCell(0).getStringCellValue();
            String lectureTitle = row.getCell(1).getStringCellValue();
            String lecturer = row.getCell(2).getStringCellValue();
            String siteName = row.getCell(3).getStringCellValue();
            String thumbnailUrl = row.getCell(4).getStringCellValue();

            // 강의에 들어갈 내용
            String hashtags = row.getCell(5).getStringCellValue();
            List<String> processedHashtags = List.of(hashtags.split(", "));
            Lecture lecture = new Lecture(lectureTitle, lecturer, siteName, lectureUrl, thumbnailUrl);
            lecture.setUser(user);
            lectureService.saveLecture(lecture);
            lectureService.manageHashtag(processedHashtags, lecture); // 강의를 생성할 때 해시태그를 넣어야 함
        }
        opcPackage.close();
        return new ResponseEntity<>(new ResponseMessage(200, "강의가 등록되었습니다"), HttpStatus.OK);
    }

    // 관리자용 강의 수정
    @PatchMapping("/{lectureId}")
    public ResponseEntity<ResponseMessage> updateLecture(@PathVariable("lectureId") Long lectureId, @RequestBody LectureDto lectureDto, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);

        if(!user.getRole().equals(Role.ADMIN)) // 관리자 유저가 아닌경우
            return new ResponseEntity<>(new ResponseMessage(403, "관리자 권한이 아닌 유저입니다"), HttpStatus.FORBIDDEN);
        // 해시태그 추가시 hashtag에 반영, lecturHashtag에도
        // 해시태그 삭제 시 lectureHashtag 에서도 빼기
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture != null) {
            lectureService.updateLecture(lectureDto, lectureId);
            return new ResponseEntity<>(new ResponseMessage(200, "강의 수정 성공"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의"), HttpStatus.NOT_FOUND);
    }

    // 관리자용 강의 삭제
    @DeleteMapping("/{lectureId}")
    public ResponseEntity<ResponseMessage> deleteLecture(@PathVariable("lectureId") Long lectureId, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        System.out.println("user.getUserId() = " + user.getUserId());
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);

        if(!user.getRole().equals(Role.ADMIN)) {// 관리자 유저가 아닌경우
            System.out.println("hi");
            return new ResponseEntity<>(new ResponseMessage(403, "관리자 권한이 아닌 유저입니다"), HttpStatus.FORBIDDEN);

        }
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture != null) {
            lectureService.deleteLecture(lectureId);
            reviewService.deleteReviews(lecture); // 리뷰 다 삭제
            return new ResponseEntity<>(new ResponseMessage(200, "강의 삭제 성공"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의"), HttpStatus.NOT_FOUND);
    }


    // 전체 강의 글 조회 + 필터링 된 강의 글 조회
    @ApiOperation(value = "전체 강의글 조회 + 검색 필터링별 강의 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동 (모든 강의리뷰 조회 / 필터링 된 강의리뷰 조회)"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "검색어", example = "자바", required = false),
            @ApiImplicitParam(name = "category", value = "카테고리", example = "백엔드", required = false),
            @ApiImplicitParam(name = "page", value = "pageable object", paramType = "query")
    })
    @GetMapping("")
    public ResponseEntity<ResponseMessage> getLectures(
            @PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        if (keyword == null && category == null) { // 모든 강의 조회
            List<AllLecturesResponse> lectures = lectureService.getLectures();
            return new ResponseEntity<>(ResponseMessage.withData(200, "모든 강의를 조회했습니다", lectures), HttpStatus.OK);

//            return new ResponseEntity<>(ResponseMessage.withData(200, "모든 강의를 조회했습니다", lectures.getContent()), HttpStatus.OK);
        } else { // 검색어별 조회 or 해시태그(카테고리)별 조회
            List<AllLecturesResponse> lectures = lectureService.getFilteredLectures(pageable, keyword, category);
            return new ResponseEntity<>(ResponseMessage.withData(200, "필터링 된 강의리뷰 조회", lectures), HttpStatus.OK);
        }
    }

    // 강의글 상세 조회
    @ApiOperation(value="강의글 상세 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동 (강의 조회)"),
            @ApiResponse(code = 404, message = "존재하지 않는 유저 or 강의"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/{lectureId}")
    public ResponseEntity<ResponseMessage> getLecture(@PathVariable("lectureId") Long lectureId, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user.getReadCount() == 5 && user.getReviewWriteStatus() == false) // 리뷰 안썼는데 5번 조회한 경우
            return new ResponseEntity<>(new ResponseMessage(403, "리뷰를 작성해야 추가 조회 가능"), HttpStatus.FORBIDDEN);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture != null) {// 강의정보가 있는 경우만
            DetailLectureResponse detailLectureResponse = lectureService.getLecture(lecture, user);

            user.updateReadCount(); // 강의 조회 시 readCount 늘려주기
            return new ResponseEntity<>(ResponseMessage.withData(200, "강의를 조회했습니다", detailLectureResponse), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의"), HttpStatus.NOT_FOUND);
    }

    // 강의 등록 요청
    @PostMapping("/request")
    public ResponseEntity<ResponseMessage> requestLecture(@RequestBody HashMap<String, String> params, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);

        String requestUrl = params.get("lectureUrl");
        LectureUrlResponse lectureUrlResponse = lectureService.getLectureUrl(requestUrl);
        if(lectureUrlResponse != null)
            return new ResponseEntity<>(new ResponseMessage(409, "이미 등록된 강의입니다.", lectureUrlResponse), HttpStatus.CONFLICT);

        RequestedLecture requestedLecture = lectureService.findByRequestedLecture(requestUrl);
        if(requestedLecture != null)
            return new ResponseEntity<>(new ResponseMessage(409, "이미 등록 요청된 강의입니다"), HttpStatus.CONFLICT);
        Long requestedLectureId = lectureService.saveRequestedLecture(requestUrl);//status 0 으로 기본 저장 (대기중인 상태)
        int crawlerStatus = lectureService.callRequestedLectureCrawler(requestUrl,requestedLectureId);
        if(crawlerStatus==-1){
            //크롤러가 존재하지 않는 사이트에 대한 요청
            return new ResponseEntity<>(new ResponseMessage(201, "강의가 요청되었습니다. 마이페이지에서 결과를 확인해주세요!"), HttpStatus.CREATED);
        }
        //크롤러가 존재하는 사이트에 대한 요청
        return new ResponseEntity<>(new ResponseMessage(201, "강의가 요청되었습니다. 마이페이지에서 결과를 확인해주세요!"), HttpStatus.CREATED);
    }

    // 리뷰 화면 들어간 경우
    @GetMapping("/{lectureId}/reviews")
    public ResponseEntity<ResponseMessage> createReviewWithLectureInfo(@PathVariable("lectureId") Long lectureId, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);

        Lecture lecture = lectureService.findById(lectureId);
        if(lecture==null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의"), HttpStatus.NOT_FOUND);
        
        LectureUrlResponse lectureUrlResponse = lectureService.getLectureUrl(lecture.getLectureUrl());
        return new ResponseEntity<>(ResponseMessage.withData(200, "존재하는 강의", lectureUrlResponse), HttpStatus.OK);
    }

    // 강의에 들어가서 리뷰 다는 경우
    @PostMapping("/{lectureId}/reviews")
    public ResponseEntity<ResponseMessage> createReview(@RequestBody ReviewDto reviewDto, @PathVariable("lectureId") Long lectureId, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);

        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의"), HttpStatus.NOT_FOUND);

        Review review = reviewService.findByUserAndLecture(user, lecture);
        if(review != null)
            return new ResponseEntity<>(new ResponseMessage(409, "리뷰 여러 번 업로드 불가"), HttpStatus.CONFLICT);

        reviewService.saveReview(reviewDto, user, lecture);
        return new ResponseEntity<>(new ResponseMessage(201, "강의 탭에서 리뷰 등록 성공"), HttpStatus.CREATED);
    }

    // 강의글 좋아요
    @PostMapping("/{lectureId}/likes")
    public ResponseEntity<ResponseMessage> createLike(@PathVariable("lectureId") Long lectureId, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);

        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의"), HttpStatus.NOT_FOUND);

        String likeStatus = likeService.changeLikeStatus(lecture, user);
        if(likeStatus.equals("like cancel"))
            return new ResponseEntity<>(new ResponseMessage(200, "좋아요 취소 성공"), HttpStatus.OK);
        else if(likeStatus.equals("like success again"))
            return new ResponseEntity<>(new ResponseMessage(200, "좋아요 재등록 성공"), HttpStatus.OK);
        else
            return new ResponseEntity<>(new ResponseMessage(201, "좋아요 등록 성공"), HttpStatus.CREATED);
    }

    // 중복링크 찾기
    @PostMapping("/url")
    public ResponseEntity<ResponseMessage> checkLectureUrl(@RequestBody HashMap<String, String> checkUrl){
        String lectureUrl = checkUrl.get("lectureUrl");
        LectureUrlResponse lectureUrlResponse = lectureService.getLectureUrl(lectureUrl);
        if(lectureUrlResponse!=null)// 중복링크가 있으면 해시태그까지 출력되도록
            return new ResponseEntity<>(ResponseMessage.withData(200, "중복된 링크가 존재합니다.", lectureUrlResponse), HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage(200, "중복된 링크가 없습니다."), HttpStatus.OK);
    }
}