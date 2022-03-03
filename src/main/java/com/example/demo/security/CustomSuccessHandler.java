package com.example.demo.security;

import com.example.demo.security.jwt.JwtTokenProvider;
import com.example.demo.user.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
        String targetUrl = determineTargetUrl(request, response, authentication);

        getRedirectStrategy().sendRedirect(request, response, checkNickname(authentication,request,targetUrl));
    }


    private String checkNickname(Authentication authentication,HttpServletRequest request,String targetUrl){
        User user=(User)request.getSession().getAttribute("user");
        Long savedUserId=user.getUserId();
        System.out.println("userID: "+savedUserId);
//        String nodeId=(String)request.getSession().getAttribute("nodeId");

        String userNickname=user.getUserNickname();

        if(userNickname.contains("_CONFLICT")){
            //중복 닉네임이 있는 유저라는 의미이므로, 새로운 닉네임 폼으로 보내주어야 한다
            return UriComponentsBuilder.fromUriString(targetUrl).path("nickname/{userId}")
                    .build().expand(savedUserId).toUriString();
        }else{
            String token = tokenProvider.generateJwtToken(authentication);
            String res=UriComponentsBuilder.fromUriString(targetUrl).path("github-login/{userId}/{token}")
                    .buildAndExpand(savedUserId,token).toUriString();
            System.out.println(res);
//            res=targetUrl+"github-login/"+savedUserId;
            return res;

        }
    }
}
