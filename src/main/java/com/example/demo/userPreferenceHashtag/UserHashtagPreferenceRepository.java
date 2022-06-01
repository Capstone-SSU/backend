package com.example.demo.userPreferenceHashtag;

import com.example.demo.hashtag.Hashtag;
import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHashtagPreferenceRepository extends JpaRepository<UserPreferenceHashtag, Long> {
    UserPreferenceHashtag findByUserAndHashtag(User user, Hashtag hashtag);
}
