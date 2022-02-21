package com.example.demo.service;

import com.example.demo.domain.Lecture;
import com.example.demo.repository.LectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LectureService {
    private LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public void saveLecture(Lecture lecture){
        lectureRepository.save(lecture);
    }
}
