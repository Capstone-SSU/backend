package com.example.demo.lecture.repository;

import com.example.demo.lecture.Lecture;
import org.springframework.data.jpa.domain.Specification;


// Criteria 를 이용해서 검색 조건을 지정하는 코드가 메소드로 추상화된다.
public class LectureSpecification {
    // 제목에 키워드 포함된 것들 가져오기
    public static Specification<Lecture> titleLike(final String keyword) {
        return (root, query, cb) ->
                cb.like(root.get("lectureTitle"), "%"+keyword+"%");
    }
    // 키워드가 해당 강의의 해시태그들 중에 속해있는 경우
//    public static Specification<Lecture> categoryMatch(final String category) {
//        // 각 강의마다 hashtag 리스트에 hashtag가 들어가 있는지
//
//        return (root, query, cb) -> {
//            ListJoin<Employee, Phone> phoneJoin = root.join(Employee_.phones);
//            ListJoin<Lecture, LectureHashtag> lectureJoin = root.join(Lecture_.lectureHashtags);
//            System.out.println("lectureJoin = " + lectureJoin);
//            Join<LectureHashtag, Hashtag> hashtagJoin = root.join("Hashtag", JoinType.INNER);
//            List<String> categories = new ArrayList<>();
//            return cb.in(category.value(lectureJoin.get("lectureId"));
//            return null;
//        };
//    }
}
