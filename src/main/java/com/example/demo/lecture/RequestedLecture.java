package com.example.demo.lecture;

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
}
