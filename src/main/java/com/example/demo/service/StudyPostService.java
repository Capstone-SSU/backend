package com.example.demo.service;

import com.example.demo.domain.StudyPost;
import com.example.demo.dto.StudyPostDTO;
import com.example.demo.repository.StudyPostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class StudyPostService {
    private final StudyPostRepository studyPostRepository;

    public long saveStudyPost(StudyPost post){
        StudyPost studyPost = studyPostRepository.save(post);
        return studyPost.getStudyPostId();
    }

    public StudyPost findStudyPostById(Long postId){
        Optional<StudyPost> post = studyPostRepository.findById(postId);
        return post.orElse(null);
    }

    public StudyPost modifyStudyPost(StudyPostDTO postDTO,Long postId){
        StudyPost post=findStudyPostById(postId);
        if(post!=null){
            post.setStudyPost(postDTO);
            saveStudyPost(post);
            post=findStudyPostById(postId);
            return post;
        }else{
            return null;
        }
    }

    public List<StudyPost> getAllStudyPosts(){
        List<StudyPost> studyPosts = studyPostRepository.findAll();
        if(studyPosts.isEmpty()){
            return studyPosts;
        }

        //remove를 하는 경우에 enchanced for loop을 사용하면? remove의 fastRemove 에서 데이터 조작으로 인한 오류 발생 -> iterator를 사용하자!
        Iterator<StudyPost> itr=studyPosts.iterator();
        while(itr.hasNext()){
            StudyPost post=itr.next();
            if(post.getStudyStatus()==0){
                itr.remove(); // iterator를 사용하는 경우, StudyPosts자체에 대해 remove를 사용하면 오류가 발생한다. -> 반드시 iterator 자체에 대해서 remove를 수행해야함
            }
        }
        return studyPosts;
    }

    public List<StudyPost> getStudyPostsByKeyword(String originKeyword){ // 만약 띄어쓰기가 들어온다면 띄어쓰기별로 다른 단어로 검색
        if(originKeyword==null){ // 해당 키워드 파라미터를 검색에 사용하지 않은 경우 null을 return
            return null;
        }
        //keyword를 포함한 내용에 대한 검색 결과 return -> 검색에는 contains 또는 indexOf 사용 (indexOf는 내부에서 contains가 호출됨)
        String[] keywords=originKeyword.split(" ");
        //만약 " "가 없다면 그냥 한 단어만 들어가겠죠,,,? 생기는 단어 개수에 따라서 다르게 결과를 찾으려면 이중포문 돌리나? LIKE 쿼리문을 쓰는게 낫나?
        List<StudyPost> allPosts=getAllStudyPosts();
        List<StudyPost> keywordPosts=new ArrayList<>();
        if(allPosts.isEmpty()){
            return allPosts;
        }
        for(String keyword:keywords){
            for(StudyPost post:allPosts){
//                System.out.println("post title: "+post.getStudyTitle());
                if(post.getStudyTitle().contains(keyword)||post.getStudyContent().contains(keyword)){
                    if(!keywordPosts.contains(post)){
                        keywordPosts.add(post);
                    }
                }
            }
        }

        return keywordPosts;
    }

}
