package com.example.demo.study.repository;

import com.example.demo.study.domain.StudyComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.demo.study.domain.QStudyComment.studyComment;

@Repository
@AllArgsConstructor
public class CustomStudyCommentRepositoryImpl implements CustomStudyCommentRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StudyComment> findNestedComments(Long groupId) {
        return jpaQueryFactory
                .selectFrom(studyComment)
                .where(studyComment.commentGroupId.eq(groupId),studyComment.commentClass.eq(1),studyComment.commentStatus.eq(1))
                .fetch();
    }
}
