package com.example.demo.lecture;

import com.example.demo.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="requested_lectures")
@Data
@NoArgsConstructor
public class RequestedLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long requestedLectureId;

    @Column
    @NotNull
    private String lectureUrl;

    @Column
    @NotNull
    private int managedStatus = 0;
    //0이면? 대기중      1이면? 등록 완료       -1이면? 크롤링 불가능한 사이트 요청 (url 에 nomad 이런거는 들어있어서 크롤러로 넘어가기는 하는데, 파싱 과정에서 문제 발생 or 나중에 크롤러 등록하려고 보니까 이상한 url 일 때)

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name="userId")
    @JsonBackReference
    private User user;

    public void modifyManagedStatus(int status){
        this.managedStatus=status;
    }

    @Builder
    public RequestedLecture(String url, User user){
        this.lectureUrl=url;
        this.user=user;
    }
}
