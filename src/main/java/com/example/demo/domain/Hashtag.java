package com.example.demo.domain;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="hashtags")
@NoArgsConstructor
@Getter
@Setter
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long hashtagId;

    @NotNull
    private String hashtagName;

    @Builder
    public Hashtag(String hashtagName) {
        this.hashtagName = hashtagName;
    }
}
