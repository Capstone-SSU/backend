package com.example.demo.lecture.repository;

import com.example.demo.lecture.Lecture;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

// Criteria 를 이용해서 검색 조건을 지정하는 코드가 메소드로 추상화된다.
public class LectureSpecs {
    // 제목에 키워드 포함된 것들 가져오기
    public static Specification<Lecture> titleLike(final String keyword) {
        return new Specification<Lecture>() {
            @Override
            public Predicate toPredicate(Root<Lecture> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(root.get("lectureTitle"), "%" + keyword + "%");
            }
        };
    }
    // 키워드가 해당 강의의 해시태그들 중에 속해있는 경우
}
