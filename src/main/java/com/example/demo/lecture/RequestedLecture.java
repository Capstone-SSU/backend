package com.example.demo.lecture;

import com.example.demo.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
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

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name="userId")
    @JsonBackReference
    private User user;
}
