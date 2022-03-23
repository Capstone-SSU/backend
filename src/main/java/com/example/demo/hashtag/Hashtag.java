package com.example.demo.hashtag;

import com.example.demo.reviewHashtag.ReviewHashtag;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="hashtags")
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(value="reviewHashtags")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long hashtagId;

    @Column
    @NotNull
    private String hashtagName;

    // hashtag : reviewHashtag = 1:N
    @OneToMany(mappedBy = "hashtag", targetEntity = ReviewHashtag.class)
    @JsonManagedReference
    private List<ReviewHashtag> reviewHashtags = new ArrayList<>();

    @Builder
    public Hashtag(String hashtagName) {
        this.hashtagName = hashtagName;
    }
}
