package com.example.demo.auth;


import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//스프링 시큐리티와 관련한 Config를 모두 이곳에 작성
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter { // 스프링 웹 보안 설정

    //비밀번호 암호화를 위해 Spring Security에서 제공하는 모듈듈
   @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManagerBean 등록 -> 하단의 configure에서 LoginForm을 이용한 자동 처리 사용 X, 수동으로 Authentication을 만들어서 SecurityContext에 저장!
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()  throws Exception{
       return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and() // 추후 cors 허용을 위해 미리 추가해둠
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/","/**/*.png","/**/*.jpg","/**/*.js","/**/*.css","/**/*.html","/**/*.gif","/**/*.svg","/signup","/signup/**","/signin").permitAll()
                .antMatchers(HttpMethod.GET,"/lectures","/lectures/**","/studies","/studies/**","/roadmaps","/roadmaps/**").permitAll()
                .antMatchers(HttpMethod.PATCH,"/lectures/**").hasRole("ADMIN") // ADMIN이라고 작성하면 자동으로 ROLE_ADMIN으로 검색이 이루어진다
                .antMatchers(HttpMethod.DELETE,"/lectures/**").hasRole("ADMIN") //ADMIN 권한을 가진 경우에만 접근 허용
                .anyRequest().authenticated(); //위를 제외한 다른 모든 요청은 권한 확인
//                .and()
//                .formLogin()
//                .loginPage("/signin")
//                .loginProcessingUrl("/signin");


    }
}
