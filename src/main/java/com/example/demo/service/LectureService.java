package com.example.demo.service;

import com.example.demo.domain.Lecture;
import com.example.demo.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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

    // 전체 강의 조회
    public List<Lecture> findAllLectures (){
        List<Lecture> lectures = lectureRepository.findAll();
        System.out.println("lectures.size() = " + lectures.size());
        return lectures!=null?lectures:Collections.emptyList();
    }

    // 특정 강의 조회
    public Lecture findById(Long lectureId){
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        return lecture.orElse(null);
    }

    // url 중복 조회용
    public Lecture findByUrl(String lectureUrl){
        Optional<Lecture> lecture = lectureRepository.findBylectureUrl(lectureUrl);
        return lecture.orElse(null);
    }
}
