package com.example.demo.roadmap.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    //한 그룹에 속해있는 모든 로드맵 데이터를 강의 order 순서대로 오름차순
    @Override
    public List<RoadMap> findAllRoadmapsByGroupId(Integer groupId) {
        List<RoadMap> roadmaps=jpaQueryFactory
                .selectFrom(roadMap)
                .where(roadMap.roadmapGroupId.eq(groupId))
                .orderBy(roadMap.roadmapLectureOrder.asc())
                .fetch();
        return roadmaps;
    }
}
