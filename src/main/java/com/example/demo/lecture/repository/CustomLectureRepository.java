package com.example.demo.lecture.repository;

import com.example.demo.lecture.dto.LectureDto;

public interface CustomLectureRepository {
//    List<Lecture> findByHashtag(List<String> categories);
    void updateLecture(LectureDto lectureDto, Long lectureId);
    void deleteLecture(Long lectureId);
}
