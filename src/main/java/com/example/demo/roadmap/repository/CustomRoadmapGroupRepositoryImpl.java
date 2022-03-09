package com.example.demo.roadmap.repository;

import com.example.demo.roadmap.RoadMapGroup;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.demo.roadmap.QRoadMapGroup.roadMapGroup;

@Repository
@AllArgsConstructor
public class CustomRoadmapGroupRepositoryImpl implements CustomRoadmapGroupRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RoadMapGroup> findAllRoadmapsWithFilter(String[] keywords) {
        return jpaQueryFactory
                .selectFrom(roadMapGroup)
                .where(predicate(keywords))
                .fetch();

    }

    private BooleanBuilder predicate(String[] keywords){
        BooleanBuilder builder=new BooleanBuilder();
        for(String keyword:keywords){
            builder.or(roadMapGroup.roadmapGroupTitle.contains(keyword))
                    .or(roadMapGroup.roadmapGroupRecommendation.contains(keyword));
        }
        return builder.and(roadMapGroup.roadmapGroupStatus.eq(1));
    }

}
