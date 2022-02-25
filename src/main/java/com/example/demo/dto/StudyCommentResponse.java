package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class StudyCommentResponse {
    //스터디 댓글을 스터디 상세글에 넣어서 return 해줄 때, 필요한 값들만 넣어서 보낼 수 있도록 만든 객체

    private Long studyCommentId;

    private String commentContent;

    private Integer commentStatus; // 1이면 존재, 0이면 삭제됨

    private LocalDateTime commentCreatedDate;

    private Integer commentClass; // 0이면 부모댓글 (원댓글), 1이면 자식댓글 (대댓글)

    private Long commentGroupId; // 몇번 댓글에 대한 모음인지 카운트 (부모댓글의 studyCommentId == 대댓글들의 groupId)
    //프론트에서 댓글 달린거 넘겨줄 때
    // 1) 원댓글(부모댓글)이면: "parent":0  으로 넘겨주고 -> 0으로 넘어오면 새롭게 등록된 애가 부모 댓글임을 확인 => 이 댓글의 pk를 groupId로 저장
    // 2) 대댓글이면: "parent":{얘의 부모댓글의 id} 로 넘겨줘야함 -> 넘어온 부모 댓글 id를 이 댓글의 groupId로 저장

    private Integer commentReportCount; // 신고횟수

    private UserOnlyDto user;
}
