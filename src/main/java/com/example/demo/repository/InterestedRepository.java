package com.example.demo.repository;

import com.example.demo.domain.Like;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestedRepository extends JpaRepository<Like, Long> {
    List<Like> findAllInterestByUser(User user); // user기반으로 모든 관심글 찾아오기
}
