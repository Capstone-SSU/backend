package com.example.demo.roadmap.service;

import com.example.demo.like.Like;
import com.example.demo.like.LikeService;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.roadmap.dto.DetailRoadmapResponse;
import com.example.demo.roadmap.repository.RoadmapGroupRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RoadmapGroupService {
    private final RoadmapGroupRepository roadmapGroupRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final LikeService likeService;

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

}
