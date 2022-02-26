package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewOnlyDto {
    private String nickname;
    private long reviewId;
    private int rate;
    private String commentTitle;
    private String comment;
    private LocalDateTime createdDate;
    private int reportCount;
    private int reviewStatus;
}
