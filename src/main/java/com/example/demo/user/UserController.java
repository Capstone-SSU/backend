package com.example.demo.user;

import com.example.demo.dto.*;
import com.example.demo.security.AuthResponse;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.RefreshTokenRepository;
import com.example.demo.security.domain.RefreshToken;
import com.example.demo.user.domain.Company;
import com.example.demo.user.domain.Role;
import com.example.demo.user.domain.User;
import com.example.demo.user.dto.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.example.demo.user.UserDetailsServiceImpl.companyKey;

@Api(tags = {"User"})
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
    public ResponseEntity<ResponseMessage> signup(@RequestBody SignupDTO signupDTO){
        String pwd= signupDTO.getPassword();
        String encodedPwd=bCryptPasswordEncoder.encode(pwd);
        String imgUrl= signupDTO.getImageUrl();
        String role = signupDTO.getRole();
        if(!userService.checkEmailValidate(signupDTO.getEmail()).equals("email valid")||!userService.checkNicknameValidate(signupDTO.getNickname()).equals("nickname valid")){
            return new ResponseEntity<>(new ResponseMessage(401,"이메일 또는 닉네임 중복체크를 진행해주세요."), HttpStatus.OK);
        }
        User user= new User(signupDTO.getName(), signupDTO.getNickname(), signupDTO.getEmail(),encodedPwd);

        if(imgUrl!=null){
            user.updateProfileImage(imgUrl);
        }

        if(role!=null&&role.equals(Role.ADMIN.name())) {
            user.updateUserRole();
        }

        userService.saveUser(user);
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
        AuthResponse authResponse=userService.authenticateLogin(email,password);
        if(authResponse!=null){
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
        AuthResponse authResponse=userService.authenticateLogin(email,password);
        if(authResponse!=null){
            return new ResponseEntity<>(ResponseMessage.withData(200, "로그인 성공", authResponse),HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResponseMessage(401,"로그인 실패"),HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/company")
    public ResponseEntity<ResponseMessage> checkUserCompany(@PathVariable Long userId, @RequestParam("email") String email){
        if(email.contains("'"))
            email=email.replace("'","");
        String domain=email.split("@")[1];
        Company company= userService.checkCompanyExistence(domain);
        if(company==null){
            return new ResponseEntity<>(new ResponseMessage(404,"지원되지 않는 소속에 대한 인증 요청 입니다."),HttpStatus.OK);
        }

        //고유번호 생성 -> 입력한 이메일로 메일 보내기
        Boolean success=userService.sendMail(userId,email,company.getCompanyName());
        if(success){
            return new ResponseEntity<>(new ResponseMessage(200,"인증 메일 전송 성공"),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ResponseMessage(400,"인증 메일 전송 실패"),HttpStatus.OK);
        }

    }

    @PostMapping("/users/{userId}/company")
    public ResponseEntity<ResponseMessage> checkCompanyEmailCertificate(@PathVariable Long userId, @RequestBody CompanyCertificateDto companyCertificateDto){
        CompanyNameKey nameAndKey = companyKey.get(userId);
        if(nameAndKey==null){
            return new ResponseEntity<>(new ResponseMessage(404,"인증메일을 다시 요청해주세요"),HttpStatus.OK);
        }
        Integer userInput=companyCertificateDto.getCertificateNumber();
        String success=userService.certificateCompanyNumber(userId,userInput,nameAndKey);
        if(success.equals("fail")){
            return new ResponseEntity<>(new ResponseMessage(200,"잘못된 인증번호를 입력하셨습니다."),HttpStatus.OK);
        }
        companyKey.remove(userId); //인증번호 매칭이 성공했을 경우에는 저장된 로컬에 저장된 정보 삭제
        return new ResponseEntity<>(new ResponseMessage(201," 사용자 소속 등록 성공, "+success),HttpStatus.OK);

    }

    @PostMapping("/reissue")
    public ResponseEntity<AuthResponse> regenerateAccessToken(@RequestBody ReissueDto reissueDto){
        AuthResponse authResponse = userService.reissue(reissueDto.getRefreshToken());
        if(authResponse==null){
//            "사용 불가능한 리프레시 토큰에 대한 재발급 요청"
            return new ResponseEntity<>(null,HttpStatus.OK);
        }
        return new ResponseEntity<>(authResponse,HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(Principal principal, HttpServletRequest request){
        String email = principal.getName();
        userService.logout(email,request);
        return new ResponseEntity<>("로그아웃 성공",HttpStatus.OK);

    }



}