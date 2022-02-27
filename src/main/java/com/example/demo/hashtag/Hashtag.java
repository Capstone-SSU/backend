package com.example.demo.hashtag;

import com.example.demo.reviewHashtag.ReviewHashtag;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    // hashtag : reviewHashtag = 1:N
    @OneToMany(mappedBy = "hashtag", targetEntity = ReviewHashtag.class)
    private List<ReviewHashtag> reviewHashtags = new ArrayList<>();

    @Builder
    public Hashtag(String hashtagName) {
        this.hashtagName = hashtagName;
    }
}
