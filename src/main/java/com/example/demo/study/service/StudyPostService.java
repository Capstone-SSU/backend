package com.example.demo.study.service;

import com.example.demo.like.LikeService;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.study.dto.AllStudyPostsResponse;
import com.example.demo.study.dto.StudyPostDTO;
import com.example.demo.study.util.StudyPostLikeCalc;
import com.example.demo.study.util.StudyPostLikeComparator;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.study.repository.StudyPostRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class StudyPostService {
    private final StudyPostRepository studyPostRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final LikeService likeService;

    public long saveStudyPost(StudyPost post){
        StudyPost studyPost = studyPostRepository.save(post);
        return studyPost.getStudyPostId();
    }

    public StudyPost findStudyPostById(Long postId){
        Optional<StudyPost> post = studyPostRepository.findById(postId); // status 0이면 null return하게 바꾸기
        if(post.isPresent()){
            StudyPost studyPost=post.get();
            if(studyPost.getStudyStatus()==0){
                return null;
            }else{
                return studyPost;
            }
        }else{
            return null;
        }
    }

    public StudyPost modifyStudyPost(StudyPostDTO postDTO,Long postId){
        StudyPost post=findStudyPostById(postId);
        if(post!=null){ //post의 status가 1인 경우에만 set
            post.setStudyPost(postDTO);
            saveStudyPost(post);
            post=findStudyPostById(postId);
            return post;
        }else{
            return null;
        }
    }

    public List<StudyPost> getAllStudyPosts(Integer recruitStatus, String sort){
        Sort postSort=getSort(sort);
        List<StudyPost> studyPosts;
        studyPosts=postSort==null?studyPostRepository.findAll():studyPostRepository.findAll(postSort);

        if(studyPosts.isEmpty()){
            return studyPosts;
        }

        Iterator<StudyPost> itr=studyPosts.iterator();
        while(itr.hasNext()){
            StudyPost post=itr.next();
            if(post.getStudyStatus()==0){
                itr.remove();
            }else if(recruitStatus!=null&&recruitStatus.equals(1)&&post.getStudyRecruitStatus()==0){ //모집중인 애들만 요구하면 얘도 바줘야함
                itr.remove();
            }
        } //studyPosts status==1, 모집중인 애들만 요구한 경우: recruitStatus==1인 애들만 남는다.

        if(sort!=null&&sort.contains("likes")){
            studyPosts=getLikeOrderedStudyPosts(studyPosts);
        }

        return studyPosts;
    }

    private Sort getSort(String sort){
        if(sort==null){
            return null;
        }
        else if(sort.contains("asc")||sort.contains("ASC")){ //오래된 순
            return Sort.by(Sort.Direction.ASC, "studyPostId");
        }else{ //최신순
            return Sort.by(Sort.Direction.DESC,"studyPostId");
        }
    }

    private List<StudyPost> getLikeOrderedStudyPosts(List<StudyPost> studyPosts){
        List<StudyPostLikeCalc> calcList=new ArrayList<>();
        for(StudyPost post:studyPosts){
            Integer likeCount=likeService.getLikeCountOnStudyPost(post);
            System.out.println("post id: "+post.getStudyPostId()+", likeCount: "+likeCount);
            calcList.add(new StudyPostLikeCalc(post,likeCount));
        }
        Collections.sort(calcList,new StudyPostLikeComparator());
        studyPosts.clear();
        for(StudyPostLikeCalc calc:calcList){
            studyPosts.add(calc.getStudyPost());
        }
        return studyPosts;
    }


    public List<StudyPost> getStudyPostsWithFilter(String originCategories, String originKeywords, String location, Integer recruitStatus, String sort){
        String[] categories=null;
        String[] keywords=null;
        if(originCategories!=null){
            categories=originCategories.split(",");
        }
        if(originKeywords!=null){
            keywords=originKeywords.split(" ");
        }
        return studyPostRepository.findPostsByTest(categories,keywords,location);
    }

    //전체 스터디글을 화면에 보여줄 때 list 데이터
    public List<AllStudyPostsResponse> getAllStudiesResponse(List<StudyPost> studyPostList){
        List<AllStudyPostsResponse> studiesResponseList=new ArrayList<>();
        for(StudyPost post:studyPostList){
            AllStudyPostsResponse studyResponse=new AllStudyPostsResponse();
            studyResponse.setStudyPostWriter(userDetailsService.getSimpleUserDto(post.getUser()));
            BeanUtils.copyProperties(post,studyResponse);
            studyResponse.setStudyLikeCount(likeService.getLikeCountOnStudyPost(post));
            studyResponse.setStudyRecruitState(post.getStudyRecruitStatus()==1?"모집중":"모집완료");
            studiesResponseList.add(studyResponse);
        }
        Collections.reverse(studiesResponseList);
        return studiesResponseList;
    }
}
