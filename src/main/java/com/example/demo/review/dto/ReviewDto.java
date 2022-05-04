package com.example.demo.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
    private String lectureUrl;
    private String comment;
    private String commentTitle;
    private int rate;
}
