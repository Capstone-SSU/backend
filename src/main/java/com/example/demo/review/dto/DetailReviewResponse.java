package com.example.demo.review.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class DetailReviewResponse {
    private String nickname;
    private long reviewId;
    private int rate;
    private String commentTitle;
    private String comment;
    private LocalDateTime createdDate;
    private int reportCount; // 이거 여기 왜있지 없어도도리 것 같은디
    private boolean writerStatus;
}
