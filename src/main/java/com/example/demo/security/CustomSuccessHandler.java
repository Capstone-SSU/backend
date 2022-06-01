package com.example.demo.security;

import com.example.demo.security.jwt.JwtTokenProvider;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    private String redirectUri="test";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        handle(request, response, authentication);
        super.clearAuthenticationAttributes(request);
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        System.out.println("handler called");
//        String targetUrl = determineTargetUrl(request, response, authentication);

        getRedirectStrategy().sendRedirect(request, response, checkNickname(authentication,request));
    }


    private String checkNickname(Authentication authentication,HttpServletRequest request){
        User user=(User)request.getSession().getAttribute("user");
        Long savedUserId=user.getUserId();
        System.out.println("userID: "+savedUserId);
//        String nodeId=(String)request.getSession().getAttribute("nodeId");

        String userNickname=user.getUserNickname();
        String targetUrl="http://moyeo.org/pickit/";

        if(userNickname.contains("_CONFLICT")){
            //중복 닉네임이 있는 유저라는 의미이므로, 새로운 닉네임 폼으로 보내주어야 한다
            return UriComponentsBuilder.fromUriString(targetUrl).path("nickname/{userId}")
                    .build().expand(savedUserId).toUriString();
            //깃허브 닉네임 충돌 시: pick-it/nickname/userId -> userId 로 사용자 닉네임 수정해서 다시 요청해야함
        }else{
            String token = tokenProvider.generateJwtToken(authentication);
            String res=UriComponentsBuilder.fromUriString(targetUrl).path("github-login/{userId}/{token}")
                    .buildAndExpand(savedUserId,token).toUriString();
            System.out.println(res);
            //로그인 성공시: github-login/userId/토큰
//            res=targetUrl+"github-login/"+savedUserId;
            return res;

        }
    }
}
