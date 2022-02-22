package com.example.demo.service;

import com.example.demo.domain.Interested;
import com.example.demo.repository.InterestedRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
