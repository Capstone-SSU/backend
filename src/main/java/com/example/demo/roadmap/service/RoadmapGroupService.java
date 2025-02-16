package com.example.demo.roadmap.service;

import com.example.demo.like.Like;
import com.example.demo.like.LikeService;
import com.example.demo.review.Review;
import com.example.demo.roadmap.RoadMap;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.roadmap.dto.AllRoadmapsResponse;
import com.example.demo.roadmap.dto.DetailRoadmapResponse;
import com.example.demo.roadmap.repository.RoadmapGroupRepository;
import com.example.demo.roadmap.repository.RoadmapSpecification;
import com.example.demo.user.domain.User;
import com.example.demo.user.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoadmapGroupService {
    private final RoadmapGroupRepository roadmapGroupRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final LikeService likeService;
    private final RoadmapService roadmapService;

    public RoadMapGroup saveRoadmapGroup(RoadMapGroup roadMapGroup){
        return roadmapGroupRepository.save(roadMapGroup);
    }

    public RoadMapGroup findRoadmapGroupById(Long groupId){
        Optional<RoadMapGroup> group = roadmapGroupRepository.findById(groupId);
        if(group.isPresent()&&group.get().getRoadmapGroupStatus()==1){
            return group.get();
        }else{
            return null;
        }
    }

    public List<RoadMapGroup> getAllRoadmapGroups(){
        List<RoadMapGroup> groups = roadmapGroupRepository.findAll();
        Iterator<RoadMapGroup> itr=groups.iterator();
        while(itr.hasNext()){
            RoadMapGroup group=itr.next();
            if(group.getRoadmapGroupStatus()==0)
                itr.remove();
        }
        return groups;
    }

    public List<AllRoadmapsResponse> getAllRoadmapGroupsWithFilter(String keyword,Pageable pageable){
        String[] keywords=keyword.split(" ");
        Page<RoadMapGroup> roadMapGroups = roadmapGroupRepository.findAll(RoadmapSpecification.getRoadmapByFilter(keywords), pageable);
        List<AllRoadmapsResponse> responseGroups=new ArrayList<>();
        for(RoadMapGroup group:roadMapGroups){
            responseGroups.add(getAllRoadmapsResponse(group));
        }
        return responseGroups;
    }

    public DetailRoadmapResponse getDetailRoadmapResponse(User user, RoadMapGroup group,User roadmapWriter){

        DetailRoadmapResponse detailRoadmapResponse=new DetailRoadmapResponse();
        detailRoadmapResponse.setRoadmapTitle(group.getRoadmapGroupTitle());
        detailRoadmapResponse.setRoadmapGroupId(group.getRoadmapGroupId());
        detailRoadmapResponse.setRoadmapRecommendation(group.getRoadmapGroupRecommendation());
        detailRoadmapResponse.setIsLikedByUser(checkRoadmapLikedByUser(user,group)!=null);
        detailRoadmapResponse.setIsThisUserRoadmapWriter(roadmapWriter.getUserId()==user.getUserId());
        detailRoadmapResponse.setRoadmapWriter(userDetailsService.getSimpleUserDto(roadmapWriter));
        detailRoadmapResponse.setLikeCount(likeService.getLikeCountOnRoadmap(group));

        return detailRoadmapResponse;
    }

    public Like checkRoadmapLikedByUser(User user,RoadMapGroup group){
        Like like=likeService.findLikeByRoadmapAndUser(user,group);
        if(like!=null&&like.getLikeStatus()==1)
            return like;
        else
            return null;
    }

    public void updateRoadmapTitleAndRecommendation(String title, String recommendation, RoadMapGroup group){
        group.setRoadmapGroupTitle(title);
        group.setRoadmapGroupRecommendation(recommendation);
        saveRoadmapGroup(group);
    }

    public Boolean validateUserLectureForRoadmap(List<Long> lectureIds, User user){
        List<Review> reviews = user.getReviews();
        List<Long> userLectureIds=new ArrayList<>();
        for(Review review:reviews){
            userLectureIds.add(review.getLecture().getLectureId());
        }
        for(Long id:lectureIds){
            if(!userLectureIds.contains(id))
                return false;
        }
        return true;
    }

    public AllRoadmapsResponse getAllRoadmapsResponse(RoadMapGroup group){
        User writer=group.getUser();
        List<RoadMap> roadMaps=roadmapService.getAllRoadMapsByGroup(group);
        List<String> thumbnails=new ArrayList<>();
        for(RoadMap roadMap:roadMaps){
            thumbnails.add(roadMap.getLecture().getThumbnailUrl());
        }
        Integer likeCountOnRoadmap = likeService.getLikeCountOnRoadmap(group);
        return new AllRoadmapsResponse(group.getRoadmapGroupId(), group.getRoadmapGroupTitle(),writer.getUserNickname(),writer.getUserCompany(),group.getRoadmapGroupCreatedDate(),thumbnails,likeCountOnRoadmap);

    }

    public List<AllRoadmapsResponse> getAllResponseByPageable(Pageable pageable){
        Page<RoadMapGroup> groups = roadmapGroupRepository.findAll(RoadmapSpecification.getExistingRoadmap(),pageable); //findAll 에 specification 포함시키기
        List<AllRoadmapsResponse> roadmapsResponses=new ArrayList<>();
        for(RoadMapGroup group:groups){
            roadmapsResponses.add(getAllRoadmapsResponse(group));
        }
        return roadmapsResponses;
    }

    public List<AllRoadmapsResponse> getAllResponseWithoutPage(){
        List<RoadMapGroup> groups = roadmapGroupRepository.findAll(RoadmapSpecification.getExistingRoadmap()); //findAll 에 specification 포함시키기
        List<AllRoadmapsResponse> roadmapsResponses=new ArrayList<>();
        for(RoadMapGroup group:groups){
            roadmapsResponses.add(getAllRoadmapsResponse(group));
        }
        return roadmapsResponses;
    }

    public List<AllRoadmapsResponse> getAllResponseWithFilterWithoutPage(String keyword){
        String[] keywords=keyword.split(" ");
        List<RoadMapGroup> roadMapGroups = roadmapGroupRepository.findAll(RoadmapSpecification.getRoadmapByFilter(keywords));
        List<AllRoadmapsResponse> responseGroups=new ArrayList<>();
        for(RoadMapGroup group:roadMapGroups){
            responseGroups.add(getAllRoadmapsResponse(group));
        }
        return responseGroups;
    }



}
