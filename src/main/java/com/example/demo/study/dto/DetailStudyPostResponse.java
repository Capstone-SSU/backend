package com.example.demo.study.dto;

import com.example.demo.like.Like;
import com.example.demo.user.dto.UserOnlyDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DetailStudyPostResponse {
    private long studyPostId;

    private String studyCategoryName;

    private String studyTitle;

    private String studyContent;

    private Integer studyRecruitStatus; //1이면 모집중, 0이면 모집완료

    private String studyLocation;

    private Integer studyMinReq;

    private Integer studyMaxReq; // null 이면 최대인원 없음

    private LocalDateTime studyCreatedDate;

    private Integer studyReportCount;

    private List<Like> likes; //해당 스터디글에 대한 좋아요 내역들을 모두 response

    private List<StudyCommentResponse> studyComments; //해당 스터디글에 대한 댓글들을 모두 필요한 정보들 + 댓글 작성한 user 정보 담아 response

    private UserOnlyDto user; //해당 스터디글을 작성한 User의 필요 정보들만 담아 response
}
