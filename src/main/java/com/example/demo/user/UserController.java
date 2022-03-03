package com.example.demo.user;

import com.example.demo.dto.*;
import com.example.demo.security.AuthResponse;
import com.example.demo.user.dto.SigninDTO;
import com.example.demo.user.dto.SignupDTO;
import com.example.demo.user.dto.UserIdDto;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

@Api(tags = { "User"})
@RestController
public class UserController {

    private UserDetailsServiceImpl userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final HttpSession httpSession;

    @Autowired
    public UserController(UserDetailsServiceImpl userService, BCryptPasswordEncoder bCryptPasswordEncoder, HttpSession httpSession){
        this.userService=userService;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.httpSession = httpSession;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@RequestBody SignupDTO signupDTO){
        String pwd= signupDTO.getPassword();
        String encodedPwd=bCryptPasswordEncoder.encode(pwd);
        String imgUrl= signupDTO.getImageUrl();

        if(!userService.checkEmailValidate(signupDTO.getEmail()).equals("email valid")||!userService.checkNicknameValidate(signupDTO.getNickname()).equals("nickname valid")){
            return new ResponseEntity<>(new ResponseMessage(401,"이메일 또는 닉네임 중복체크를 진행해주세요."), HttpStatus.OK);
        }
        User user= new User(signupDTO.getName(), signupDTO.getNickname(), signupDTO.getEmail(),encodedPwd);

        if(imgUrl!=null){
            user.updateProfileImage(imgUrl);
        }
        Long savedId=userService.saveUser(user);

        return new ResponseEntity<>(new ResponseMessage(201,"회원가입 성공"), HttpStatus.CREATED);
    }

    @GetMapping("/signup")
    public  ResponseEntity<ResponseMessage> emailCheck(@RequestParam("email") String email){
        System.out.println("email = " + email);
        String valid=userService.checkEmailValidate(email);
        if(valid.equals("email valid")){
            return new ResponseEntity<>(new ResponseMessage(200,"이메일 사용가능"), HttpStatus.OK);
        }else if(valid.equals("email github")){
            return new ResponseEntity<>(new ResponseMessage(403,"깃허브로 소셜로그인 된 이메일"), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ResponseMessage(409,"이미 사용중인 이메일"), HttpStatus.OK);
        }
    }

    @GetMapping("/signup/{nickname}")
    public ResponseEntity<ResponseMessage> nicknameCheck(@PathVariable String nickname){

        String valid=userService.checkNicknameValidate(nickname);
        if(valid.equals("nickname valid")){
            return new ResponseEntity<>(new ResponseMessage(200,"닉네임 사용가능"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(409,"이미 사용중인 닉네임"), HttpStatus.OK);
    }

    @GetMapping("/signup/{userId}/{nickname}")
    public ResponseEntity<ResponseMessage> nicknameUpdate(@PathVariable Long userId, @PathVariable String nickname){
        String valid=userService.checkNicknameValidate(nickname);

        if(valid.equals("nickname valid")){
            System.out.println("nickname valid");
            //user nickname update 하는 코드 추가
            User user=userService.findUserById(userId);
            user.setUserNickname(nickname);
            userService.saveUser(user); //update database

            user=userService.findUserById(userId);
            String newNickname=user.getUserNickname();

            return new ResponseEntity<>(new ResponseMessage(200,newNickname+" 으로 닉네임 변경 완료! 깃허브로 재로그인 해주세요."), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(409,"이미 사용중인 닉네임"), HttpStatus.OK);
    }


    // signin을 하면서 email과 password가 동일하면 client에게 token을 return 해준다.
    @PostMapping("/signin")
    public ResponseEntity<ResponseMessage> signin(@RequestBody SigninDTO signinDTO){
        String email= signinDTO.getEmail();
        String password= signinDTO.getPassword();
        String jwtToken=userService.authenticateLogin(email,password);
        if(jwtToken!=null){
            User foundUser=userService.findUserByEmail(email);
            Long userId=foundUser.getUserId();

            AuthResponse authResponse=new AuthResponse(jwtToken,userId);
            return new ResponseEntity<>(ResponseMessage.withData(200, "로그인 성공", authResponse),HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResponseMessage(401,"로그인 실패"),HttpStatus.OK);
    }

    @GetMapping("/temp-login-success")
    public ResponseEntity<ResponseMessage> test(HttpServletResponse response, Principal principal) {
        String email=principal.getName();
//        System.out.println("email = " + email);
        User user=userService.findUserByEmail(email);
        Long userId=user.getUserId();

        //로그인 상태 유지 확인 테스트 성공
        UserIdDto userIdDto=new UserIdDto(userId);
        System.out.println("default success url called");
        return new ResponseEntity<>(ResponseMessage.withData(200, "로그인 성공",userIdDto),HttpStatus.OK);
    }


    @PostMapping("/deploy-test")
    public ResponseEntity<ResponseMessage> deployTEST(@RequestBody SigninDTO signinDTO){
        String email= signinDTO.getEmail();
        String password= signinDTO.getPassword();
        String jwtToken=userService.authenticateLogin(email,password);
        if(jwtToken!=null){
            User foundUser=userService.findUserByEmail(email);
            Long userId=foundUser.getUserId();
            AuthResponse authResponse=new AuthResponse(jwtToken,userId);
            return new ResponseEntity<>(ResponseMessage.withData(200, "로그인 성공", authResponse),HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResponseMessage(401,"로그인 실패"),HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ResponseMessage> resignMembership(@PathVariable Long userId){
        User user=userService.findUserById(userId);
        if(user==null){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 회원에 대한 탈퇴 요청"),HttpStatus.OK);
        }else{
            userService.deleteUserById(userId);
            return new ResponseEntity<>(new ResponseMessage(200,"회원탈퇴 성공"),HttpStatus.OK);
        }
    }

}