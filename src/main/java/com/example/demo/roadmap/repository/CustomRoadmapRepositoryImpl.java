package com.example.demo.roadmap.repository;

import com.example.demo.lecture.Lecture;
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

    @Override
    public void updateExistingRoadmaps(List<Lecture> lectures, RoadMapGroup group) {
        List<RoadMap> existingRoadmapsToUpdate=jpaQueryFactory
                .selectFrom(roadMap)
                .where(roadMap.roadmapGroup.eq(group),roadMap.lecture.in(lectures))
                .fetch();

        for(RoadMap roadmap:existingRoadmapsToUpdate){
            Lecture lecture=roadmap.getLecture();
            int index=lectures.indexOf(lecture);
            roadmap.setRoadmapStatus(1);
            roadmap.updateRoadmapLectureOrder(index+1);
        }

        List<RoadMap> roadmapsToDelete=jpaQueryFactory
                .selectFrom(roadMap)
                .where(roadMap.roadmapGroup.eq(group),roadMap.roadmapStatus.eq(1),roadMap.lecture.notIn(lectures))
                .fetch();

        for(RoadMap roadmap:roadmapsToDelete){
            roadmap.setRoadmapStatus(0);
        }

    }

    @Override
    public List<RoadMap> findRoadmapsByGroupAndLectures(RoadMapGroup group, List<Lecture> lectures) {
        return jpaQueryFactory
                .selectFrom(roadMap)
                .where(roadMap.roadmapGroup.eq(group),roadMap.lecture.in(lectures))
                .fetch();
        }



}
