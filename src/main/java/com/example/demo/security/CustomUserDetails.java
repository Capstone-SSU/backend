package com.example.demo.security;

import com.example.demo.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//Spring Security에서 아래의 객체에 User Entity 정보를 삽입, Authentication 형태로 SeurityContext에 저장한다. -> 이걸로 로그인 상태 관리
@Getter
@Setter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user; // Authentication 안에 넣을 User 정보 entity

    //github 소셜 로그인 시 거기서 얻어온 정보 저장하기 위한 것
    private Map<String,Object> attributes;

    public CustomUserDetails(User user){
        this.user=user;
    }



    //현재 유저가 가지고 있는 권한 목록을 반환한다
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection=new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_" + user.getRole(); // ADMIN, USER 중 하나가 넘어옴
                //spring security에서는 ROLE_USER 와 같은 형태로 권한을 비교하므로 앞에 ROLE_을 붙여준다
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getUserPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}
