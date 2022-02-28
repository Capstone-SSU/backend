package com.example.demo.study.dto;

import com.example.demo.user.dto.SimpleUserDto;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class StudyCommentResponse {
    //스터디 댓글을 스터디 상세글에 넣어서 return 해줄 때, 필요한 값들만 넣어서 보낼 수 있도록 만든 객체
    //여기에 부모댓글 (원댓글) 정보를 담고, 안에 자식댓글들을 list 로 담기

    private Long studyCommentId;

    private SimpleUserDto commentWriter; //여기 안에 현재 로그인한 사용자가 해당 댓글의 작성자인지, 아닌지를 표시

    private Boolean isThisCommentWriterPostWriter;

    private Boolean isThisUserCommentWriter; // 현재 사용자가 글을 작성한 사람인가 -> 스터디 상세글 조회에 글과 댓글 모두에 들어가야함

    private String commentContent;

    private LocalDateTime commentCreatedDate;

    private List<StudyCommentResponse> nestedComments; // 이 부모댓글에대한 자식 댓글들 (자식댓글들 자체에는 얘가 null로 설정)





}
