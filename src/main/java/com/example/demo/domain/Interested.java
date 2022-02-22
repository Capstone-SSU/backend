package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "interested")
@Data
@NoArgsConstructor // jpa에는 기본 contructor가 필요함 -> 없을 경우에 "No default constructor for Entity 에러 발생" -> NoArgsConstructor로 해결 가능
public class Interested {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interestedId;

    @Column
    private Integer targetDivision; // 0이면 스터디글, 1이면 강의글, 2면 로드맵

    @Column(columnDefinition = "integer default 0")
    private Integer interestedStatus=1; // 1이 default, 0은 삭제된 글

    //하나의 글에 여러개의 관심글 -> 관심글이 M, 주인
    @ManyToOne(fetch = FetchType.EAGER,targetEntity = StudyPost.class)
    @JsonBackReference
    @JoinColumn(name = "studyPost_studyPostId")
    private StudyPost studyPost;

    //한 명의 사용자가 여러개의 관심글 등록 -> 관심글이 M, 주인
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JsonBackReference
    @JoinColumn(name = "user_userId")
    private User user;

    //강의컬럼도 추가하까 마까 하다가 일단 안햇슴다,,,,,! ㅇ,<

    @Builder
    public Interested(User user, Integer division){
        this.targetDivision=division;
        this.user=user;
    }
}
