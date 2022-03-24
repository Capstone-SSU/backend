package com.example.demo.lectureHashtag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureHashtagService {
    private final LectureHashtagRepository lectureHashtagRepository;
    public long saveLectureHashtag(LectureHashtag reviewHashtag){
        LectureHashtag savedLectureHashtag = lectureHashtagRepository.save(reviewHashtag);
        return savedLectureHashtag.getLectureTagId();
    }
}
