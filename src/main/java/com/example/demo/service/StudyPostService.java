package com.example.demo.service;

import com.example.demo.domain.StudyPost;
import com.example.demo.repository.StudyPostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if(post.isPresent()){
            return post.get();
        }else{
            return null;
        }
    }

}
