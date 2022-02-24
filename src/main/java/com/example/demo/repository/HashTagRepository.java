package com.example.demo.repository;

import com.example.demo.domain.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashTagRepository extends JpaRepository<Hashtag, Long>, CustomHashtagRepository{
}
