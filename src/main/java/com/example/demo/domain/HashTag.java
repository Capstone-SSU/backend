package com.example.demo.domain;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="hashTags")
@NoArgsConstructor
@Getter
@Setter
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long hashTagId;

    @NotNull
    private String hashTagName;

    @Builder
    public HashTag(String hashTagName) {
        this.hashTagName = hashTagName;
    }
}
