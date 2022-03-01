package com.example.demo.roadmap.repository;

import com.example.demo.roadmap.QRoadMap;
import com.example.demo.roadmap.RoadMap;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.demo.roadmap.QRoadMap.roadMap;

@Repository
@AllArgsConstructor
public class CustomRoadMapRepositoryImpl implements CustomRoadMapRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Integer findMaxGroupId() {
        Integer groupId=jpaQueryFactory
                .selectFrom(roadMap)
                .select(roadMap.roadmapGroupId)
                .orderBy(roadMap.roadmapGroupId.desc()) //groupId를 내림차순 정렬 -> 가장 첫번째 값, 즉 가장 큰 groupId를 가져옴
                .fetchFirst();
        return groupId;

    }
}
