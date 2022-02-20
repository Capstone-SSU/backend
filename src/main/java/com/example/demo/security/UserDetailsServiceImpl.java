package com.example.demo.security;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserDetailsServiceImpl(UserRepository userRepository, @Lazy AuthenticationManager authManager, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider) {

        this.userRepository = userRepository;
        this.authManager=authManager;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User findUserByEmail(String email){
        return userRepository.findByUserEmail(email); //없으면 null, 있으면 user 객체 return
    }

    public User findUserById(Long id){
        Optional<User> user=userRepository.findById(id);
        return user.orElse(null);
    }

    public String checkEmailValidate(String email){
        User user=userRepository.findByUserEmail(email);
        if(user==null){
            return "email valid";
        }else if(user.getLoginProvider()!=null&&user.getLoginProvider().equals("GITHUB")){
            return "email github";
        }else{
            return "email conflict";
        }
    }

    public String checkNicknameValidate(String nickname){
        User user=userRepository.findByUserNickname(nickname);
        if(user!=null){
            return "nickname conflict";
        }else{
            return "nickname valid";
        }
    }

    public Long saveUser(User user){
        return userRepository.save(user).getUserId();
    }

    public String authenticateLogin(String email, String pwd){
        User user=userRepository.findByUserEmail(email);
        if(user==null){
            return null;
        }
        if(!bCryptPasswordEncoder.matches(pwd,user.getUserPassword())){
            //전달 파라미터가 암호화 되지 않은 비밀번호
            return null;
        }

        //Authentication Token 생성 (username, password) 사용
        //여기서 username: 중복되지 않는 고유값 -> email로 대체하여 사용
        UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(email,pwd);
        Authentication auth=authManager.authenticate(authToken);
//        SecurityContextHolder.getContext().setAuthentication(auth);

        return jwtTokenProvider.generateJwtToken(auth);

    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //spring security에서 로그인 된 사용자의 정보를 이 메소드를 통해 가져와, security context에 저장한다!
        User user=userRepository.findByUserEmail(email);
        return new CustomUserDetails(user);
        //CustomUserDetails는 Authentication type으로 생각하는게 이해하기 쉬움
        //User Entity 정보와 authority 등 부가 정보 함께 가짐
    }
}
