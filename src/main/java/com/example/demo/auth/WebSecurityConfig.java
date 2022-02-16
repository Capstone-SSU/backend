package com.example.demo.auth;


import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//스프링 시큐리티와 관련한 Config를 모두 이곳에 작성
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter { // 스프링 웹 보안 설정

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().disable();
        http.authorizeRequests()
                .antMatchers("/","/css/*","/img/*","/js/*","/signup/*","/signin/*").permitAll()
                .antMatchers(HttpMethod.GET,"/lectures/*","/studies/*","/roadmaps/*").permitAll()
//                .antMatchers(HttpMethod.PATCH,"/lectures/*").hasRole(Role.ADMIN)
//                .antMatchers(HttpMethod.DELETE,"/lectures/*").hasRole(Role.ADMIN)
                .anyRequest().authenticated(); //위를 제외한 다른 모든 요청은 권한 확인


    }
}
