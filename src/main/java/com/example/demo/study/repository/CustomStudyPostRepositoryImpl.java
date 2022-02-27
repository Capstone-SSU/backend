package com.example.demo.study.repository;

import com.example.demo.study.domain.StudyPost;
import com.querydsl.core.BooleanBuilder;
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
    public List<StudyPost> findPostsByTest(String[] categories, String[] keywords, String location) {
        BooleanBuilder booleanBuilder=new BooleanBuilder();
        //모든 categories에 대해 or조건을 더해준다
        List<StudyPost> postList = jpaQueryFactory
                .selectFrom(studyPost)
                .where(predicate(categories, keywords, location))
                .fetch();

        return postList; // 조회된게 없으면 empty list가 return 됨

    }

    private BooleanBuilder predicate(String[] categories, String[] keywords, String location){
        BooleanBuilder builder=new BooleanBuilder();
        if(categories!=null){
            builder.and(checkCategory(categories));
        }
        if(keywords!=null){
            builder.and(checkKeyword(keywords));
        }
        if(location!=null){
            builder.and(studyPost.studyLocation.eq(location));
        }
        return builder.and(studyPost.studyStatus.eq(1));
    }

    private BooleanBuilder checkCategory(String[] categories){
        BooleanBuilder booleanBuilder=new BooleanBuilder();
        for(String category:categories){
            booleanBuilder.or(studyPost.studyCategoryName.eq(category));
        }
        return booleanBuilder;
    }

    private BooleanBuilder checkKeyword(String[] keywords){
        BooleanBuilder booleanBuilder=new BooleanBuilder();
        for(String keyword:keywords){
            booleanBuilder.or(studyPost.studyContent.contains(keyword));
            booleanBuilder.or(studyPost.studyTitle.contains(keyword)); // 이게 맞냐,,,,
        }
        return booleanBuilder;
    }


}
