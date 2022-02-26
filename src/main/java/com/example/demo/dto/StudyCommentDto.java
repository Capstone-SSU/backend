package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StudyCommentDto {
    private String commentContent;
    private Integer commentClass;
    private Long commentParentId;
}
