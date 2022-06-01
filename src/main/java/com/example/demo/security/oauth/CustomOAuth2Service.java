package com.example.demo.security.oauth;

import com.example.demo.user.domain.User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.user.UserDetailsServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Service

public class CustomOAuth2Service extends DefaultOAuth2UserService {
    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final HttpSession httpSession;

    public CustomOAuth2Service(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder, HttpSession httpSession) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.httpSession = httpSession;
    }


    //Oauth 소셜 로그인 후처리 담당 메소드
    //로그인 성공 -> 깃허브로 부터 access token을 받아온거 까지가 파라미터인 userRequest
    //userRequest에 담긴 정보로 깃허브를 통해 회원 프로필을 받아오는게 loadUser 메소드의 역할! -> loadUser가 어디서 호출되는건지,,,,,

    /**
     * loadUser의 return값인 CustomUserDetail 객체가 Authentication으로 Spring Security Context에 저장된다! -> 세션에 저장된다 생각하면 됨
     * 따라서, 저장하기 전에 사용자의 정보를 다시 입력받으려면 (회원가입 창에 정보를 넣어두고 거기에 추가 정보를 입력받는것) loadUser 메소드가 return되기 전에 처리해야하는데,,,,,, 이걸 도대체 어케하지
     * */

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
//        System.out.println("clientRegistration: "+userRequest.getClientRegistration()); // -> registrationId: 어떤 oauth 로그인인지
//        System.out.println("accessToken: "+userRequest.getAccessToken());
        /**
          * 깃헙 로그인 클릭 -> 깃허브의 로그인 창이 뜨고 -> 로그인 성공하고 -> code를 return하고 ->
          *그 code를 oauth library가 받고 -> access token을 요청하고
          *까지가 UserRequest 정보!
          *이제부터 loadUser를 사용해서 userRequest 정보를 사용해서 회원 프로필, accessToken을 받아와야 한다
        */

//        System.out.println("getAttributes: "+super.loadUser(userRequest).getAttributes());
        OAuth2User oAuth2User=super.loadUser(userRequest);
        return getOAuth2UserInfo(userRequest,oAuth2User); //OAuth2User를 return 한다 -> GithubOAuth2User로 바꿔주고 프론트에 값을 넘긴다!
    }

    private CustomUserDetails getOAuth2UserInfo(OAuth2UserRequest userRequest, OAuth2User oAuth2User){
        String provider=userRequest.getClientRegistration().getClientId();
        System.out.println("provider = " + provider);
//        if(!provider.equals("github")){
//            throw new InternalAuthenticationServiceException(String.format("Only Github OAuth2 provider is supported"));
//        }
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String nickname=attributes.get("login").toString();
        String githubUsername=nickname;
        String email=nickname+"@github.com";
        if(attributes.get("email")!=null){
            email=attributes.get("email").toString();
        }
        String nodeId=attributes.get("node_id").toString(); // password 대신에 사용자별 고유값 node_id를 암호화하여 password 필드에 저장
        String pwd=bCryptPasswordEncoder.encode(nodeId);

        User user=userDetailsService.findUserByEmail(email);

        if(user==null){
            //새로운 회원, 즉 회원가입일 경우에만 유저 저장
            String profileUrl=null;
            if(attributes.get("avatar_url")!=null){
                profileUrl=attributes.get("avatar_url").toString();
            }
            String name=attributes.get("name").toString();

            String nicknameCheck=userDetailsService.checkNicknameValidate(nickname);
            if(nicknameCheck.equals("nickname conflict")){
                //새롭게 회원가입한 회원의 깃허브 username이 이미 디비에 있는 닉네임과 충돌된다면?
                nickname=nickname+"_CONFLICT";
            }


            user=new User(name,nickname,email,pwd);
            user.updateProfileImage(profileUrl);
            user.setGithubProvider("GITHUB");
            user.updateGithubUrlName(githubUsername); //nickname == username (깃허브 사용자는 자동으로 등록)
            userDetailsService.saveUser(user);
        }

        //새로운 회원이 아닌 경우에는 바로 CustomUserDetails에 반영

        CustomUserDetails customUserDetails=new CustomUserDetails(user);
        customUserDetails.setAttributes(oAuth2User.getAttributes()); // 나중에 여기서 필요 정보 빼가서 쓸 수 있음

        httpSession.setAttribute("user",user); // /nickname에서 방금 로그인 한 사용자 정보 찾기 위해
        httpSession.setAttribute("nodeId",nodeId);


        return customUserDetails;

    }

}
