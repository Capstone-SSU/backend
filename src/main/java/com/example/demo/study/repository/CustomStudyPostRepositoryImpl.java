package com.example.demo.study.repository;

import com.example.demo.mypage.dto.MyStudiesResponse;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.domain.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.demo.study.domain.QStudyPost.studyPost;

@Repository
@AllArgsConstructor
public class CustomStudyPostRepositoryImpl implements CustomStudyPostRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StudyPost> findPostsWithFilter(String[] categories, String[] keywords, String location,Integer recruitStatus, String sort) {
        OrderSpecifier<Long> orderSpecifier=studyPost.studyPostId.desc();
        if(sort!=null&&(sort.contains("ASC")||sort.contains("asc"))){
            orderSpecifier=studyPost.studyPostId.asc();
        }

        return jpaQueryFactory
                .selectFrom(studyPost)
                .where(predicate(categories, keywords, location,recruitStatus))
                .orderBy(orderSpecifier)
                .fetch();

    }

    private BooleanBuilder predicate(String[] categories, String[] keywords, String location,Integer recruitStatus) {
        BooleanBuilder builder = new BooleanBuilder();
        if (categories != null) {
            builder.and(checkCategory(categories));
        }
        if (keywords != null) {
            builder.and(checkKeyword(keywords));
        }
        if (location != null) {
            builder.and(studyPost.studyLocation.eq(location));
        }
        if(recruitStatus!=null&&recruitStatus.equals(1)){
            builder.and(studyPost.studyRecruitStatus.eq(1));
        }
        return builder.and(studyPost.studyStatus.eq(1));
    }

    private BooleanBuilder checkCategory(String[] categories) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String category : categories) {
            booleanBuilder.or(studyPost.studyCategoryName.eq(category));
        }
        return booleanBuilder;
    }

    private BooleanBuilder checkKeyword(String[] keywords) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String keyword : keywords) {
            booleanBuilder.or(studyPost.studyContent.contains(keyword));
            booleanBuilder.or(studyPost.studyTitle.contains(keyword)); // 이게 맞냐,,,,
        }
        return booleanBuilder;
    }

    @Override
    public List<MyStudiesResponse> findByUser(User user) {
        return jpaQueryFactory
                .select(Projections.constructor(MyStudiesResponse.class,
                                studyPost.studyPostId,
                                studyPost.studyTitle,
                                studyPost.studyLocation,
                                studyPost.studyCategoryName,
                                studyPost.studyRecruitStatus))
                .from(studyPost)
                .where(studyPost.user.eq(user), studyPost.studyStatus.eq(1))
                .fetch();
    }
}
