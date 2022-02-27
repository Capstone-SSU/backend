package com.example.demo.hashtag.repository;

import com.example.demo.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>, CustomHashtagRepository {
    Optional<Hashtag> findByHashtagName(String hashtagName);
    Optional<Hashtag> findById(Long hashtagId);
}
