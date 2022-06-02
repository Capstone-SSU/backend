package com.example.demo.security.jwt;

import com.example.demo.security.RefreshTokenRepository;
import com.example.demo.security.domain.RefreshToken;
import com.example.demo.user.domain.User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.user.UserDetailsServiceImpl;
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

    @Value("${auth.jwtSecret}")
    private  String secretKey;


    private long accessTokenValidTime = 1000L * 60 * 3; // accessToken 유효시간: 1시간 (테스트용: 3분)
    private long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 7; // refreshToken 유효시간: 7일

    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenProvider(@Lazy UserDetailsServiceImpl userDetailsService, RefreshTokenRepository refreshTokenRepository) {
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // JWT 토큰 생성
    public String generateJwtToken(User user,String tokenType) {
        //"ACCESS" or "REFRESH"
//        System.out.println("token secret key: "+secretKey);

        Long userId=user.getUserId();
        String userEmail=user.getUserEmail();
        String userRole =user.getRole().name();
        System.out.println("generate jwt token");


        byte[] signKey=secretKey.getBytes(StandardCharsets.UTF_8);
        //claims에는 사용자와 관련된 내용들 중 jwt에 포함할 내용을 지정해서 저장
        //여기서는 email, role, userid

        //이 인증된 user가 가지고 있는 authority를 list 형태로 get

        Date now = new Date();

        return Jwts.builder()
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + (tokenType.equals("ACCESS")?accessTokenValidTime:refreshTokenValidTime))) // set Expire Time
                .signWith(Keys.hmacShaKeyFor(signKey), SignatureAlgorithm.HS512)
                .setId(UUID.randomUUID().toString())
                .setIssuer("PickIT")
                .setHeaderParam("typ","JWT")
                .claim("email",userEmail)
                .claim("userId",userId)
                .claim("role",userRole)
                .compact();
    }

    public String createAccessToken(User user){
        return generateJwtToken(user,"ACCESS");
    }

    public String createAndSaveRefreshToken(User user){
        String refreshToken=generateJwtToken(user,"REFRESH");
        refreshTokenRepository.save(RefreshToken.generateRefreshToken(user.getUserEmail(),refreshToken,refreshTokenValidTime));
        return refreshToken;
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

    public Long remainingTimeInToken(String token){
        Claims claims = this.parseToken(token);
        Date now=new Date();
        return claims.getExpiration().getTime()-now.getTime();

    }

    public Claims parseToken(String token){
        byte[] signKey=secretKey.getBytes(StandardCharsets.UTF_8);
        return Jwts.parserBuilder()
                .setSigningKey(signKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
