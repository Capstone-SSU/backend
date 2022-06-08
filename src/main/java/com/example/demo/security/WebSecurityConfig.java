package com.example.demo.security;


import com.example.demo.security.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.security.jwt.JwtAuthenticationFilter;
import com.example.demo.security.jwt.JwtTokenProvider;
import com.example.demo.security.oauth.CustomOAuth2Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//스프링 시큐리티와 관련한 Config를 모두 이곳에 작성
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter { // 스프링 웹 보안 설정

    private final CustomOAuth2Service customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtEntryPoint;

    public WebSecurityConfig(@Lazy CustomOAuth2Service customOAuth2UserService, JwtAuthenticationFilter jwtAuthenticationFilter, CustomSuccessHandler customSuccessHandler, JwtTokenProvider jwtTokenProvider, JwtAuthenticationEntryPoint jwtEntryPoint) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtEntryPoint = jwtEntryPoint;
    }

    //비밀번호 암호화를 위해 Spring Security에서 제공하는 모듈듈


    //AuthenticationManagerBean 등록 -> 하단의 configure에서 LoginForm을 이용한 자동 처리 사용 X, 수동으로 Authentication을 만들어서 SecurityContext에 저장!
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()  throws Exception{
       return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new CustomSuccessHandler(jwtTokenProvider);
        handler.setUseReferer(true);
        return handler;
    }

    private static final String[] PERMIT_URL_ARRAY = {
            /* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/lectures/admin", // 알고리즘 테스트용
            "/lectures/data", // 알고리즘 입력용
            "/reissue", //이거 추가 왜 해야하는걸까,,,,의문
            "/hashtags/test"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and() // 추후 cors 허용을 위해 미리 추가해둠
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Preflight Request 허용해주기
                .antMatchers("/","/**/*.png","/**/*.jpg","/**/*.js","/**/*.css","/**/*.html","/**/*.gif","/**/*.svg","/signup","/signup/**","/signin","/oauth2/**","/nickname","/login/**").permitAll()
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
                .antMatchers(HttpMethod.GET,"/lectures","/studies","/roadmaps","/hashtags").permitAll()
                .antMatchers(HttpMethod.PATCH,"/lectures/**").hasRole("ADMIN") // ADMIN이라고 작성하면 자동으로 ROLE_ADMIN으로 검색이 이루어진다
                .antMatchers(HttpMethod.DELETE,"/lectures/**").hasRole("ADMIN") //ADMIN 권한을 가진 경우에만 접근 허용
                .anyRequest().authenticated() //위를 제외한 다른 모든 요청은 권한 확인
//                .and()
//                .formLogin()
//                .loginPage("http://localhost:3000/login") // 권한 없는 사용자가 페이지에 접근하면? -> 프론트의 login 라우터로 연결 (로그인 페이지)
                .and()
                .oauth2Login().userInfoEndpoint().userService(customOAuth2UserService)
                .and()
                .successHandler(successHandler())
//                .defaultSuccessUrl("/nickname",true) // GetMapping의 /nickname으로 가서 깃허브 유저네임 중복 체크!
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtEntryPoint)
                .and()
                .logout().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


    }
}
