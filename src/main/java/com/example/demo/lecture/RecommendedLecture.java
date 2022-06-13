package com.example.demo.lecture;

import com.example.demo.lecture.Lecture;
import com.example.demo.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="recommended_lectures")
@Data
@NoArgsConstructor
public class RecommendedLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long recommendedLectureId;

    @Column
    @NotNull
    @CreatedDate
    private LocalDateTime createdDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Lecture.class)
    @JoinColumn(name="lecture_id")
    @JsonBackReference // 연관관계의 주인
    @NotNull
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name="userId")
    @JsonBackReference
    private User user;
}
