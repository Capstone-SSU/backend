package com.example.demo.service;

import com.example.demo.domain.Interested;
import com.example.demo.domain.StudyPost;
import com.example.demo.domain.User;
import com.example.demo.repository.InterestedRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class InterestedService {
    private final InterestedRepository interestedRepository;

    public void saveInterest(Interested interested){
        interestedRepository.save(interested);
    }

    public Interested findInterestById(Long id){
        Optional<Interested> interest = interestedRepository.findById(id);
        return interest.orElse(null);

    }

    //studyPost와 user로 찾는게 있어야함
    public Interested findInterestByStudyPostandUser(StudyPost post, User user){
        //user로 먼저 찾고 그 다음에 거기 내에서 studyPost로 찾는거
        List<Interested> interestsByUser=interestedRepository.findAllInterestByUser(user);

        //list에서 특정 StudyPost 한 개를 찾는 기능 다른데서도 사용되면 메소드로 따로 빼두기
        Interested interested=null;
        for (Interested interest : interestsByUser) {
            if (interest.getStudyPost() == post) {
                interested = interest;
                break;
            }
        }
        return interested;

    }
}
