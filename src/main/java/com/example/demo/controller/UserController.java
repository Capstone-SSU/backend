package com.example.demo.controller;

import com.example.demo.auth.CustomUserDetails;
import com.example.demo.dataObject.vo.SigninVO;
import com.example.demo.dataObject.vo.SignupVO;
import com.example.demo.domain.User;
import com.example.demo.dataObject.dto.ResponseMessage;
import com.example.demo.auth.UserDetailsServiceImpl;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @GetMapping("/signup/{userId}/{nickname}")
    public ResponseEntity<ResponseMessage> nicknameUpdate(@PathVariable Long userId, @PathVariable String nickname, @AuthenticationPrincipal CustomUserDetails customUserPrincipal){
        String valid=userService.checkNicknameValidate(nickname);

        if(valid.equals("nickname valid")){
            System.out.println("nickname valid");
            //user nickname update 하는 코드 추가
            User user=userService.findUserById(userId);
            user.setUserNickname(nickname);
            userService.saveUser(user); //update database

            user=userService.findUserById(userId);
            String newNickname=user.getUserNickname();

            return new ResponseEntity<>(new ResponseMessage(200,"닉네임 변경 완료, "+newNickname), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(409,"이미 사용중인 닉네임"), HttpStatus.CONFLICT);
    }


    @PostMapping("/signin")
    public ResponseEntity<ResponseMessage> signin(@RequestBody SigninVO signinVO, HttpServletRequest request){
        String email=signinVO.getEmail();
        String password=signinVO.getPassword();
        Authentication auth=userService.verifyLoginInfo(email,password);
        if(auth!=null){
            //login success
            SecurityContextHolder.getContext().setAuthentication(auth);
            //고유값인 이메일, 거기에 매칭되는 pwd로 AuthenticationToken을 만들면 거기에 매칭되는 CustomUserDetails객체 (Authentication type이라 생각하는게 편하다) 를 SecurityContext에 저장한다!
            CustomUserDetails principal=(CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user=principal.getUser();

            return new ResponseEntity<>(ResponseMessage.withData(200, "로그인 성공", user),HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResponseMessage(401,"로그인 실패"),HttpStatus.UNAUTHORIZED);
    }


    @GetMapping("/")
    public String landing(){
        return "landing page";
    }

    @GetMapping("/temp-login-success")
    public ResponseEntity<ResponseMessage> test(@AuthenticationPrincipal CustomUserDetails customUserDetails, HttpServletResponse response) throws IOException{

        //로그인 상태 유지 확인 테스트 성공
        User user=customUserDetails.getUser();
        System.out.println("default success url called");
//        String redirect_uri="http://localhost:3000/testPage";
//        response.sendRedirect(redirect_uri);
        return new ResponseEntity<>(ResponseMessage.withData(200, "로그인 성공", user),HttpStatus.OK);
    }

    // 깃허브 회원가입시 닉네임 중복을 체크하는 API -> 프론트에서 해당 라우터가 완성되면 동작하도록 해당 api를 호출하는 코드는 주석처리 해둠
    @GetMapping("/nickname")
    public void checkGithubJoinNicknameConflict(@AuthenticationPrincipal CustomUserDetails customUserDetails, HttpServletResponse response) throws IOException {
        //SecurityContext에 User 정보가 저장된 후 여기로 넘어오는 것임
        Long savedUserId=customUserDetails.getUser().getUserId(); // 방금 깃허브 회원가입으로 인해 저장된 User의 계정
        User user=userService.findUserById(savedUserId);
        String userNickname=user.getUserNickname();
        String redirect_uri="http://localhost:3000"; // http://localhost:3000/{react route} -> 로그인 후 디폴트 페이지가 들어감
        if(userNickname.contains("_CONFLICT")){
            //중복 닉네임이 있는 유저라는 의미이므로, 새로운 닉네임 폼으로 보내주어야 한다
            redirect_uri+="/nickname/"+savedUserId; // 닉네임폼(프론트에서 제작한)
            System.out.println("github join, conflicted nickname! Redirect: "+redirect_uri);
            response.sendRedirect(redirect_uri);
            // 프론트에서 닉네임입력폼을 /nickname/{userId}로 라우팅 해놓았다면, 여기로 redirect 요청을 보냈을 때 해당 페이지가 띄워지는가 (즉 스프링에서 리액트의 router에 요청을 보낼 수 있는가)
            //이후 프론트의 닉네임폼에서 닉네임을 새롭게 입력하고 -> /signup/{userId}/{nickname} 으로 GET 요청
        }else{
            //닉네임이 정상인 회원 (자체 회원가입 or 깃허브 username 중복없음 or 깃허브 로그인 회원)
            //그냥 로그인 후 페이지로 자동 redirect
            redirect_uri+="/main"; // 프론트에서 설정한 로그인 후 첫 페이지
            System.out.println("not a conflicted nickname, Redirect: "+redirect_uri);
            response.sendRedirect(redirect_uri);
        }
        //중복 닉네임이면 닉네임에 username_CONFLICT를 저장
    }


}