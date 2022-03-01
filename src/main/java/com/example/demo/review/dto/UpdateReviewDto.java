package com.example.demo.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 리뷰 수정용
public class UpdateReviewDto {
    String commentTitle;
    String comment;
}
