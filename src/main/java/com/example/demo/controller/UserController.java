package com.example.demo.controller;

import com.example.demo.auth.CustomUserDetails;
import com.example.demo.dataObject.vo.SigninVO;
import com.example.demo.dataObject.vo.SignupVO;
import com.example.demo.domain.User;
import com.example.demo.dataObject.dto.ResponseMessage;
import com.example.demo.auth.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.util.Collection;

@RestController
public class UserController {

    private UserDetailsServiceImpl userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserController(UserDetailsServiceImpl userService, BCryptPasswordEncoder bCryptPasswordEncoder){
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

        User user= new User(signupVO.getName(),signupVO.getNickname(),signupVO.getEmail(),encodedPwd);
        if(imgUrl!=null){
            user.updateProfileImage(imgUrl);
        }

        Long savedId=userService.saveUser(user);
        return "signup success";
    }

    @GetMapping("/signup/{email:.+}/")
    public  ResponseEntity<ResponseMessage> emailCheck(@PathVariable("email") String email){
//        System.out.println("email = " + email);
        String valid=userService.checkEmailValidate(email);
        if(valid.equals("email valid")){
            return new ResponseEntity<>(new ResponseMessage(200,"이메일 사용가능"), HttpStatus.OK);
        }else if(valid.equals("email github")){
            return new ResponseEntity<>(new ResponseMessage(400,"깃허브로 소셜로그인 된 이메일"), HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(new ResponseMessage(409,"이미 사용중인 이메일"), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/signup/{nickname}")
    public ResponseEntity<ResponseMessage> nicknameCheck(@PathVariable String nickname){
        String valid=userService.checkNicknameValidate(nickname);
        if(valid.equals("nickname valid")){
            return new ResponseEntity<>(new ResponseMessage(200,"닉네임 사용가능"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(409,"이미 사용중인 닉네임"), HttpStatus.CONFLICT);
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseMessage> signin(@RequestBody SigninVO signinVO){
        String email=signinVO.getEmail();
        String password=signinVO.getPassword();
        Authentication auth=userService.verifyLoginInfo(email,password);
//        System.out.println("authentication: "+auth.getPrincipal());
        if(auth!=null){
            //login success
            //User 정보 빼오는 부분 중복 코드 없애기
            SecurityContextHolder.getContext().setAuthentication(auth);
            //고유값인 이메일, 거기에 매칭되는 pwd로 AuthenticationToken을 만들면 거기에 매칭되는 CustomUserDetails객체 (Authentication type이라 생각하는게 편하다) 를 SecurityContext에 저장한다!
            CustomUserDetails principal=(CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user=principal.getUser();
            //SecurityContext에 Authentication 정보 저장
            return new ResponseEntity<>(ResponseMessage.withData(200, "로그인 성공", user),HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResponseMessage(401,"로그인 실패"),HttpStatus.UNAUTHORIZED);
    }


    @GetMapping("/")
    public String landing(){
        return "landing page";
    }

    @GetMapping("/review")
    public String test(){
        //로그인 상태 유지 확인 테스트 성공
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails userDetails=(CustomUserDetails) principal;
        String username = userDetails.getUsername();
        System.out.println("email = " + username); // email이 반환됨
        User user=userDetails.getUser();
        String nickname=user.getUserNickname();
        String imageUrl=user.getUserProfileImg();
        System.out.println("nickname = " + nickname);
        System.out.println("imageUrl = " + imageUrl);
        return "success";
    }
}
