package com.example.demo.user.repository;

import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserEmail(String email);
    User findByUserNickname(String nickname);
    void deleteByUserId(Long userId);
}
