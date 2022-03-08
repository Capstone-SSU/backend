package com.example.demo.roadmap.repository;

import com.example.demo.roadmap.RoadMap;
import com.example.demo.roadmap.RoadMapGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.demo.roadmap.QRoadMap.roadMap;

@Repository
@AllArgsConstructor
public class CustomRoadmapRepositoryImpl implements CustomRoadmapRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RoadMap> findAllRoadmapsByGroup(RoadMapGroup group) {
        return jpaQueryFactory
                .selectFrom(roadMap)
                .where(roadMap.roadmapGroup.eq(group),roadMap.roadmapStatus.eq(1))
                .orderBy(roadMap.roadmapLectureOrder.asc())
                .fetch();
    }
}
