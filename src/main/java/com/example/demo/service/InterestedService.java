package com.example.demo.service;

import com.example.demo.domain.Like;
import com.example.demo.domain.StudyPost;
import com.example.demo.domain.User;
import com.example.demo.repository.InterestedRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class InterestedService {
    private final InterestedRepository interestedRepository;

    public void saveInterest(Like like){
        interestedRepository.save(like);
    }

    public Like findInterestById(Long id){
        Optional<Like> interest = interestedRepository.findById(id);
        return interest.orElse(null);

    }

    //studyPost와 user로 찾는게 있어야함
    public Like findInterestByStudyPostandUser(StudyPost post, User user){
        //user로 먼저 찾고 그 다음에 거기 내에서 studyPost로 찾는거
        List<Like> interestsByUser=interestedRepository.findAllInterestByUser(user);

        //list에서 특정 StudyPost 한 개를 찾는 기능 다른데서도 사용되면 메소드로 따로 빼두기
        Like like =null;
        for (Like interest : interestsByUser) {
            if (interest.getStudyPost() == post) {
                like = interest;
                break;
            }
        }
        return like;

    }
}
