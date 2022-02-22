package com.example.demo.service;

import com.example.demo.domain.Lecture;
import com.example.demo.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureService {
    private final LectureRepository lectureRepository;

    public void saveLecture(Lecture lecture){
        lectureRepository.save(lecture);
    }
}
