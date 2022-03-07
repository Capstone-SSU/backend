package com.example.demo.lecture.dto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RecLecturesResponse {
    private long lectureId;
    private String lectureTitle;
    private double avgRate; // 별점
    private int reviewCnt;
    private List<String> hashtags; // 해시태그 가장 많은 3개 리스트
}
// [’강의 번호’,’강의 제목’,’평점’,’리뷰를 한 사용자의 수’,’키워드(해시)’]