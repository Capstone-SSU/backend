package com.example.demo.lecture;
import com.example.demo.dto.*;
import com.example.demo.lecture.dto.*;
import com.example.demo.like.Like;
import com.example.demo.like.LikeService;
import com.example.demo.review.Review;
import com.example.demo.review.dto.ReviewPostDto;
import com.example.demo.review.ReviewService;
import com.example.demo.user.Role;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.user.User;
import com.example.demo.userPreferenceHashtag.UserPreferenceHashtagService;
import com.nimbusds.jose.shaded.json.JSONObject;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.regexp.RE;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.time.LocalDateTime;
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

    // 추천 알고리즘 전송용 메소드
    @PostMapping("/admin")
    public String endDataForRecommend(Pageable pageable) {
        List<RecLecturesResponse> recLectures = lectureService.manageRecommendData(pageable);
        String url = "http://127.0.0.1:5000/recommend"; // flask로 보낼 url
        StringBuffer stringBuffer = new StringBuffer();
        String sb = "";
        try {
            JSONObject reqParams = new JSONObject();
            reqParams.put("data", recLectures);
            // Java 에서 지원하는 HTTP 관련 기능을 지원하는 URLConnection
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoOutput(true); //Post인 경우 데이터를 OutputStream으로 넘겨 주겠다는 설정

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            //데이터 전송
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write(reqParams.toString());

            os.flush();
            // 전송된 결과를 읽어옴
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb = sb + line + "\n";
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "send ok";
    }

    // 관리자용 강의 등록
    @PostMapping("")
    public ResponseEntity<ResponseMessage> createLecture(Principal principal) throws IOException, InvalidFormatException {
        // 현재 로그인한 사용자 아이디 가져오기
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        System.out.println("user.getRole() = " + user.getRole());
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
            Page<AllLecturesResponse> lectures = lectureService.getLectures(pageable);
            return new ResponseEntity<>(ResponseMessage.withData(200, "모든 강의를 조회했습니다", lectures.getContent()), HttpStatus.OK);
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
        // 현재 로그인한 사용자 아이디 가져오기
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        String requestUrl = params.get("lectureUrl");
        // 이미 등록된 강의
        LectureUrlResponse lectureUrlResponse = lectureService.getLectureUrl(requestUrl);
        if(lectureUrlResponse != null)
            return new ResponseEntity<>(new ResponseMessage(409, "이미 등록된 강의입니다.", lectureUrlResponse), HttpStatus.CONFLICT);
        // 이미 등록 요청된 강의
        RequestedLecture rqLecture = lectureService.findRequestedLecture(requestUrl);
        if(rqLecture != null)
            return new ResponseEntity<>(new ResponseMessage(409, "이미 등록 요청된 강의입니다"), HttpStatus.CONFLICT);
        lectureService.saveRequestedLecture(requestUrl);
        return new ResponseEntity<>(new ResponseMessage(201, "강의가 요청되었습니다."), HttpStatus.CREATED);
    }

    // 강의에 들어가서 리뷰 다는 경우
    @PostMapping("/{lectureId}/reviews")
    public ResponseEntity<ResponseMessage> createReview(@RequestBody ReviewPostDto reviewPostDto, @PathVariable("lectureId") Long lectureId, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture!=null){ // 강의가 있는 경우
            Review review = reviewService.findByUserAndLecture(user, lecture);
            if(review == null) { // 리뷰 등록한 적 없는 경우
                review = new Review();
                review.setLectureReview(reviewPostDto, user, lecture); // 바꾸고싶음
                reviewService.saveReview(review);
                lectureService.setAvgRate(lecture, review.getRate()); // 특정 강의의 평점 업뎃
                if(user.getReviewWriteStatus() == false) // 리뷰 등록했으면 status = true 로 변경
                    user.updateReviewStatus();
                return new ResponseEntity<>(new ResponseMessage(201, "강의 탭에서 리뷰 등록 성공"), HttpStatus.CREATED);
            }
            else
                return new ResponseEntity<>(new ResponseMessage(409, "리뷰 여러 번 업로드 불가"), HttpStatus.CONFLICT);
        }
        else
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의"), HttpStatus.NOT_FOUND);
    }

    // 강의글 좋아요
    @PostMapping("/{lectureId}/likes")
    public ResponseEntity<ResponseMessage> createLike(@PathVariable("lectureId") Long lectureId, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);

        Lecture lecture = lectureService.findById(lectureId);
        if(lecture!=null) { // 강의정보가 있는 경우
            Like existedLike = likeService.findLikeByLectureAndUser(lecture, user);
            if(existedLike!=null) { // 좋아요가 존재하는 경우
                if(existedLike.getLikeStatus()==1) { // 이미 눌려있는 경우
                    existedLike.changeLikeStatus(0);
                    preferenceHashtagService.updateUserPreferenceHashtag(user,lecture,-1);
                    return new ResponseEntity<>(new ResponseMessage(200, "좋아요 취소 성공"), HttpStatus.OK);
                }
                else {
                    existedLike.changeLikeStatus(1);
                    preferenceHashtagService.updateUserPreferenceHashtag(user,lecture,1);
                    return new ResponseEntity<>(new ResponseMessage(200, "좋아요 재등록 성공"), HttpStatus.OK);
                }
            }
            else {// 좋아요 처음 누른 경우
                Like like = new Like(lecture, user);
                likeService.saveLike(like);
                preferenceHashtagService.updateUserPreferenceHashtag(user,lecture,1);
                return new ResponseEntity<>(new ResponseMessage(201, "좋아요 등록 성공"), HttpStatus.CREATED);
            }
        }
        return new ResponseEntity<>(new ResponseMessage(404, "해당하는 강의가 없습니다"), HttpStatus.NOT_FOUND);
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