package com.example.demo.review.dto;

import com.example.demo.lecture.Lecture;
import com.example.demo.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewPostDto {
    private String comment;
    private String commentTitle;
    private int rate;
}
