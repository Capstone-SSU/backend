package com.example.demo.lecture.repository;

import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

// Criteria 를 이용해서 검색 조건을 지정하는 코드가 메소드로 추상화된다.
@RequiredArgsConstructor
public class LectureSpecs {
    // 제목에 키워드 포함된 것들 가져오기
    public static Specification<Lecture> titleLike(final String keyword) {
        return new Specification<Lecture>() {
            @Override
            public Predicate toPredicate(Root<Lecture> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(root.get("lectureTitle"), "%"+keyword+"%");
            }
        };
    }
    // 키워드가 해당 강의의 해시태그들 중에 속해있는 경우
    public static Specification<Lecture> categoryMatch(final List<String> category) {
        final LectureService lectureService;
        return new Specification<Lecture>() {
            @Override
            public Predicate toPredicate(Root<Lecture> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.or(root.get("lectureId").in(category));
            }
        };
    }
}
