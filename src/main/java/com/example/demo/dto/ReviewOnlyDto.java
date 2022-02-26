package com.example.demo.dto;

import com.example.demo.domain.Lecture;
import com.example.demo.domain.Report;
import com.example.demo.domain.ReviewHashtag;
import com.example.demo.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReviewOnlyDto {
    private long reviewId;
    private int rate;
    private String commentTitle;
    private String comment;
    private LocalDateTime createdDate;
    private int reportCount;
    private int reviewStatus;
}
