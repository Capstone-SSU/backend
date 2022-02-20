package com.example.demo.controller;

import com.example.demo.auth.CustomUserDetails;
import com.example.demo.dataObject.dto.AuthResponse;
import com.example.demo.dataObject.vo.SigninVO;
import com.example.demo.dataObject.vo.SignupVO;
import com.example.demo.domain.User;
import com.example.demo.dataObject.dto.ResponseMessage;
import com.example.demo.auth.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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


    // signin을 하면서 email과 password가 동일하면 client에게 token을 return 해준다.
    @PostMapping("/signin")
    public ResponseEntity<ResponseMessage> signin(@RequestBody SigninVO signinVO){
        String email=signinVO.getEmail();
        String password=signinVO.getPassword();
        String jwtToken=userService.authenticateLogin(email,password);
        if(jwtToken!=null){
            AuthResponse authResponse=new AuthResponse(jwtToken);
            return new ResponseEntity<>(ResponseMessage.withData(200, "로그인 성공", authResponse),HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResponseMessage(401,"로그인 실패"),HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/temp-login-success")
    public ResponseEntity<ResponseMessage> test(HttpServletResponse response) {


        //로그인 상태 유지 확인 테스트 성공
        System.out.println("default success url called");
        return new ResponseEntity<>(new ResponseMessage(200, "로그인 성공"),HttpStatus.OK);
    }

    // 깃허브 회원가입시 닉네임 중복을 체크하는 API -> 프론트에서 해당 라우터가 완성되면 동작하도록 해당 api를 호출하는 코드는 주석처리 해둠
    @GetMapping("/nickname")
    public void checkGithubJoinNicknameConflict(HttpServletRequest request,HttpServletResponse response) throws IOException {

        User user=(User)request.getSession().getAttribute("user");
        Long savedUserId=user.getUserId();
        String nodeId=(String)request.getSession().getAttribute("nodeId");

        String userNickname=user.getUserNickname();
        String redirect_uri="http://localhost:3000"; // http://localhost:3000/{react route}

        if(userNickname.contains("_CONFLICT")){
            //중복 닉네임이 있는 유저라는 의미이므로, 새로운 닉네임 폼으로 보내주어야 한다
            redirect_uri+="/nickname/"+savedUserId; // 닉네임폼(프론트에서 제작한)
            System.out.println("github join, conflicted nickname! Redirect: "+redirect_uri);
            response.sendRedirect(redirect_uri);

            // 프론트에서 닉네임입력폼을 /nickname/{userId}로 라우팅 해놓았다면, 여기로 redirect 요청을 보냈을 때 해당 페이지가 띄워지는가 (즉 스프링에서 리액트의 router에 요청을 보낼 수 있는가)
            //이후 프론트의 닉네임폼에서 닉네임을 새롭게 입력하고 -> /signup/{userId}/{nickname} 으로 GET 요청
        }else{
            //닉네임이 정상인 회원 (자체 회원가입 or 깃허브 username 중복없는 새 회원 or 깃허브 이미 등록 -> 이번에 로그인한 회원)
            //그냥 로그인 후 페이지로 자동 redirect
            String jwtToken=userService.authenticateLogin(user.getUserEmail(),nodeId);
            redirect_uri+="/github-login/"+jwtToken; // token 암호화 추가
            System.out.println("not a conflicted nickname, Redirect: "+redirect_uri);
            response.sendRedirect(redirect_uri);

        }
        //중복 닉네임이면 닉네임에 username_CONFLICT를 저장
        //프론트에서 fetch를 통해 Response 받아올 수 있음
    }




}