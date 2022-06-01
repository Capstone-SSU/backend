package com.example.demo.userPreferenceHashtag;

import com.example.demo.hashtag.Hashtag;
import com.example.demo.lecture.Lecture;
import com.example.demo.lectureHashtag.LectureHashtag;
import com.example.demo.lectureHashtag.LectureHashtagRepository;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserPreferenceHashtagService {
    private final UserHashtagPreferenceRepository preferenceRepository;
    private final LectureHashtagRepository lectureHashtagRepository;

    //사용자가 강의에 좋아요 누르거나 취소할 때 마다 수행 -> 좋아요 누른 경우: likeStatus == 1, 취소한 경우: likeStatus == -1
    public void updateUserPreferenceHashtag(User user, Lecture lecture,int likeStatus){
        List<LectureHashtag> lectureHashtags = lectureHashtagRepository.findByLecture(lecture);
        for(LectureHashtag lectureHashtag:lectureHashtags){
            Hashtag hashtag = lectureHashtag.getHashtag();
            UserPreferenceHashtag userHashtag = findExistingUserHashtag(hashtag, user);
            if(userHashtag==null){
                userHashtag=UserPreferenceHashtag.builder()
                        .hashtag(hashtag).user(user).build();
            }else{
                userHashtag.updatePreference(likeStatus);
            }
            preferenceRepository.save(userHashtag);
        }
    }

    private UserPreferenceHashtag findExistingUserHashtag(Hashtag hashtag, User user){
        return preferenceRepository.findByUserAndHashtag(user,hashtag);
    }
}
