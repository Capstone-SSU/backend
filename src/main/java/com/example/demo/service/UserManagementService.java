package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserManagementService {

    private final UserRepository userRepository;

    public UserManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email){
        return userRepository.findByUserEmail(email);
    }

    public String checkEmailValidate(String email){
        User user=userRepository.findByUserEmail(email);
        if(user!=null){
            return "email conflict";
        }else{
            return "email validate";
        }
    }

    public String checkNicknameValidate(String nickname){
        User user=userRepository.findByUserNickname(nickname);
        if(user!=null){
            return "nickname conflict";
        }else{
            return "nickname validate";
        }
    }

    public Long saveUser(User user){
        return userRepository.save(user).getUserId();
    }
}
