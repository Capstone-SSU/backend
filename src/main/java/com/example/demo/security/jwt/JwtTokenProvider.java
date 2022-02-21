package com.example.demo.security.jwt;

import com.example.demo.domain.User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

//    private String secretKey = "bea86c63ded6f54bb30b78ba21556739dadabcfa96b706187af38668c5ab65b7b0343e7a6b151a4146e4fe089fe3387151ac79e93300dd4064c8cd5ea99e971e";
    @Value("${auth.jwtSecret}")
    private  String secretKey;


    private long tokenValidTime = 1000L * 60 * 60; // 위와 마찬가지

    private final UserDetailsServiceImpl userDetailsService;

    public JwtTokenProvider(@Lazy UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // JWT 토큰 생성
    public String generateJwtToken(Authentication auth) {
        System.out.println("token secret key: "+secretKey);

        //authencitaion에서 로그인한 user의 정보 가져오기
        CustomUserDetails customUserDetails=(CustomUserDetails) auth.getPrincipal();
        User user=customUserDetails.getUser();
        Long userId=user.getUserId();
        String userEmail=user.getUserEmail();
        String userRole=user.getRole();
        System.out.println("generate jwt token");


        byte[] signKey=secretKey.getBytes(StandardCharsets.UTF_8);
        //claims에는 사용자와 관련된 내용들 중 jwt에 포함할 내용을 지정해서 저장
        //여기서는 email, role, userid

        //이 인증된 user가 가지고 있는 authority를 list 형태로 get

        Date now = new Date();

        return Jwts.builder()
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
                .signWith(Keys.hmacShaKeyFor(signKey), SignatureAlgorithm.HS512)
                .setId(UUID.randomUUID().toString())
                .setIssuer("PickIT")
                .setHeaderParam("typ","JWT")
                .claim("email",userEmail)
                .claim("userId",userId)
                .claim("role",userRole)
                .compact();
    }

    // 프론트에서 로그인 후 jwtToken받은 후, ['Authorization']="Bearer "+jwtToken 으로 axios header가 설정된 상태로 서버에 데이터 요청
    public String getJwtTokenFromRequestHeader(HttpServletRequest request) {
        String token;
        String tokenHeader=request.getHeader("Authorization"); // axios.defaults.header.common['Authorization']='Bearer "+token -> front
        if(StringUtils.hasText(tokenHeader)&&tokenHeader.startsWith("Bearer ")){
//            System.out.println("found token starts with bearer");
            token=tokenHeader.replace("Bearer ","");
            return token;
        }

        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public Jws<Claims> validateToken(String jwtToken) {
        try {
//            System.out.println("jwtToken = " + jwtToken);
            byte[] signKey=secretKey.getBytes(StandardCharsets.UTF_8);
            return Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(jwtToken);
        } catch (Exception e) {
            System.out.println("Token Validation Request Failed");
            e.printStackTrace();
            return null;
        }
    }

}
