package com.example.demo.domain;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="lectures")
@Data
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private long lectureId;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name="userId")
    @NotNull
    private long userId;

    @Column()
    @NotNull
    private String lectureTitle;

    @Column()
    @NotNull
    private String lecturer;

    @Column()
    @NotNull
    private String siteName;

    @Column()
    @NotNull
    private String lectureUrl;

    @Column()
    @NotNull
    private String thumbnailUrl;

    @Builder
    public Lecture(String lectureTitle, String lecturer, String siteName, String lectureUrl, String thumbnailUrl) {
        this.lectureTitle = lectureTitle;
        this.lecturer = lecturer;
        this.siteName = siteName;
        this.lectureUrl = lectureUrl;
        this.thumbnailUrl = thumbnailUrl;
    }
}
