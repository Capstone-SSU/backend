package com.example.demo.lecture;

import com.example.demo.lecture.dto.AllLecturesForRecommendResponse;
import com.example.demo.lecture.dto.AllLecturesResponse;
import com.example.demo.lecture.dto.LikedLecturesForRecommendResponse;
import com.example.demo.lecture.repository.LectureRepository;
import com.example.demo.lecture.repository.RecommendedLectureRepository;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.dto.UserIdDto;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecommendService {
    private final LectureRepository lectureRepository;
    private final RecommendedLectureRepository recommendedLectureRepository;
    private final LikeRepository likeRepository;
    private final LectureService lectureService;

    // 추천용 강의 데이터 가공 함수
    public List<AllLecturesForRecommendResponse> manageAllData() {
        /*
            ’강의 번호’,
            ’강의 제목’,
            ’평점’,
            ’리뷰를 한 사용자의 수’,
            ’키워드(해시)’
        */
        List<AllLecturesForRecommendResponse> lectures = lectureRepository
                .findAll()
                .stream()
                .map(AllLecturesForRecommendResponse::from)
                .collect(Collectors.toList());

        lectures.forEach(lecture ->
                lecture.setHashtags(lectureService.getHashtags(lecture.getLectureId()))
        );
        return lectures;
    }

    // 사용자가 좋아요한 강의 데이터 데이터 가공 함수
    public List<LikedLecturesForRecommendResponse> manageLikedData(User user) {
        /*
            ’강의 번호’,
            ’강의에 해당하는 해시태그'
        */

        List<LikedLecturesForRecommendResponse> likedLectures = likeRepository
                .findLectureLikeByUser(user)
                .stream()
                .map(LikedLecturesForRecommendResponse::from)
                .collect(Collectors.toList());

        likedLectures.forEach(lecture ->
                lecture.setHashtags(lectureService.getHashtags(lecture.getLectureId()))
        );
        return likedLectures;
    }

    // 추천된 강의 받아오기
    public List<AllLecturesResponse> getRecommendedData(User user) {
        List<RecommendedLecture> rec = recommendedLectureRepository.findByUser(user);
        
        // 추천할 거 없는 경우 추가해야 함
        return rec
                .stream()
                .skip(rec.size()-10)
                .map(rc -> AllLecturesResponse.from(rc.getLecture()))
                .collect(Collectors.toList());
    }

    // 사용자에게 현재 좋아요 관련 작업을 한 유저 정보 보내기
    public void sendUserInfoAboutLike(User user){
        String url = "http://127.0.0.1:5000/recommend"; // flask로 보낼 url
        UserIdDto userIdDto = new UserIdDto(user.getUserId());
        sendData(url, userIdDto);
    }

    public String sendData(String url, UserIdDto userIdDto) {
        String sb = "";
        try {
            JSONObject reqParams = new JSONObject();
            reqParams.put("data", userIdDto);
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

            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line = null;

            while ((line = br.readLine()) != null) {
                sb = sb + line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "send ok";
    }

}
