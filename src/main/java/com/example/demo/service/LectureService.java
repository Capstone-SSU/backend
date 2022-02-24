package com.example.demo.service;

import com.example.demo.domain.Lecture;
import com.example.demo.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureService {
    private final LectureRepository lectureRepository;

    public long saveLecture(Lecture lecture){
        Lecture savedLecture = lectureRepository.save(lecture);
        return savedLecture.getLectureId();
    }

    // url 중복 조회용
    public Lecture findByUrl(String lectureUrl){
        Optional<Lecture> lecture = lectureRepository.findByLectureUrl(lectureUrl);
        if(lecture.isPresent())
            return lecture.get();
        return null;
    }


}
