package com.example.demo.repository;

import com.example.demo.domain.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>, CustomHashtagRepository{
    Optional<Hashtag> findByHashtagName(String hashtagName);
}
