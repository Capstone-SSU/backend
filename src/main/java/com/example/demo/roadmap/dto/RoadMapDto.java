package com.example.demo.roadmap.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoadMapDto {
    private String roadmapTitle;
    private String roadmapRecommendation;
    private List<Long> lectureIds; //프론트한테 강의 순서대로 list 에 담아 넘겨줄 수 있는지 물어봐야할듯

}
