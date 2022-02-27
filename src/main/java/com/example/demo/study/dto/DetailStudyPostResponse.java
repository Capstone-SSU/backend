package com.example.demo.study.dto;

import com.example.demo.user.dto.SimpleUserDto;
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

    private String studyRecruitState; //1이면 모집중, 0이면 모집완료 -> 문자열로 바꿔서 보내주기 (모집중 or 모집완료) ok

    private String studyLocation;

    private Integer studyMinReq;

    private Integer studyMaxReq; // null 이면 최대인원 없음

    private LocalDateTime studyCreatedDate;

    private Integer likeCount; // 해당 스터디글에 대한 좋아요 갯수

    private Boolean isLikedByUser; // 현재 사용자가 이 스터디글에 좋아요를 눌렀는가

    private Boolean isThisUserPostWriter; // 현재 사용자가 글을 작성한 사람인가 -> 스터디 상세글 조회에 글과 댓글 모두에 들어가야함

    private List<StudyCommentResponse> studyComments; //해당 스터디글에 대한 댓글들을 모두 필요한 정보들 + 댓글 작성한 user 정보 담아 response

    private SimpleUserDto user; //해당 스터디글을 작성한 User의 필요 정보들만 담아 response
}
