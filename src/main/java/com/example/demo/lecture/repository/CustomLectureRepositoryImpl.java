package com.example.demo.lecture.repository;

import com.example.demo.lecture.dto.LectureDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.demo.lecture.QLecture.lecture;

@Repository
@RequiredArgsConstructor
public class CustomLectureRepositoryImpl implements CustomLectureRepository{
    private final JPAQueryFactory jpaQueryFactory;
//
//    @Override
//    public List<Lecture> findByHashtag(List<String> categories){
//        // categories 안에 해당 강의의 해시태그가 있는지
//        jpaQueryFactory
//                .selectFrom(lecture)
//                .join(lecture.lectureHashtags, lectureHashtags)
//                .fetchJoin()
//                .
//                .where(lecture.lectureHashtags.contains(categories));
//    }


    @Override
    public void updateLecture(LectureDto lectureDto, Long lectureId) {
        jpaQueryFactory
                .update(lecture)
                .set(lecture.lectureUrl, lectureDto.getLectureUrl())
                .set(lecture.lectureTitle, lectureDto.getLectureTitle())
                .set(lecture.lecturer, lectureDto.getLecturer())
                .set(lecture.siteName, lectureDto.getSiteName())
                .set(lecture.thumbnailUrl, lectureDto.getThumbnailUrl())
                .where(lecture.lectureId.eq(lectureId))
                .execute();
    }

    @Override
    public void deleteLecture(Long lectureId){
        jpaQueryFactory
                .delete(lecture)
                .where(lecture.lectureId.eq(lectureId))
                .execute();
    }
}
