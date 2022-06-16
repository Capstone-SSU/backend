package com.example.demo.mypage.dto;

import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.RequestedLecture;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestedLectureResponse {
    private Long requestedId;
    private String url;
    private String status; //success: 등록 완료 wait: 대기중 error: 잘못된 url 요청
    private Long lectureId; //등록 완료 된 애들이면 이 값 포함, url 선택 시 바로 등록 완료된 강의의 상세 페이지로 넘어가도록

    public static RequestedLectureResponse fromEntity(RequestedLecture req, Lecture lecture){
        String status;
        if(req.getManagedStatus()==0){
            status="대기중";
        }else if(req.getManagedStatus()==1){
            status="완료";
        }else{
            status="잘못된 url";
        }
        return RequestedLectureResponse.builder()
                .requestedId(req.getRequestedLectureId())
                .url(req.getLectureUrl())
                .status(status)
                .lectureId(lecture!=null?lecture.getLectureId():null)
                .build();
    }
}
