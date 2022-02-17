package com.example.demo.controller;

import com.example.demo.dataObject.vo.SignupVO;
import com.example.demo.domain.User;
import com.example.demo.dataObject.dto.ResponseMessage;
import com.example.demo.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class UserController {

    private UserManagementService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserController(UserManagementService userService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userService=userService;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }

    @PostMapping("/signup")
    public String test(@RequestBody SignupVO signupVO){
//        HashMap<String, Object> params
//        System.out.println("params = " + params);
//        String name=(String) params.get("name");
//        String nickname=(String) params.get("nickname");
//        String email=(String) params.get("email");
        String pwd= signupVO.getPassword();
        String encodedPwd=bCryptPasswordEncoder.encode(pwd);
        String imgUrl=signupVO.getImageUrl();
        System.out.println("imgUrl = " + imgUrl);



        User user= new User(signupVO.getName(),signupVO.getNickname(),signupVO.getEmail(),encodedPwd);
        if(imgUrl!=null){
            user.updateProfileImage(imgUrl);
        }

        Long savedId=userService.saveUser(user);
        return "signup success";
    }

    @GetMapping("/signup")
    public  ResponseEntity<ResponseMessage> emailCheck(@RequestParam("email") String email){
        String valid=userService.checkEmailValidate(email);
        if(valid.equals("email validate")){
            return new ResponseEntity<>(new ResponseMessage(200,"이메일 사용가능"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(409,"이메일 중복"), HttpStatus.CONFLICT);
    }

    @GetMapping("/signup/{nickname}")
    public ResponseEntity<ResponseMessage> nicknameCheck(@PathVariable String nickname){
        String valid=userService.checkNicknameValidate(nickname);
        if(valid.equals("nickname validate")){
            return new ResponseEntity<>(new ResponseMessage(200,"닉네임 사용가능"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(409,"닉네임 중복"), HttpStatus.CONFLICT);
    }


    @GetMapping("/")
    public String landing(){
    return "landing page";
    }
}
