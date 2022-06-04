package com.example.demo.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@Data
@RedisHash("logoutToken")
@AllArgsConstructor
@Builder
public class LogoutToken {

    @Id
    private String id;

    private String email;

    @TimeToLive
    private Long expiration;

    public static LogoutToken of(String accessToken, String email, Long remainingSec){
        return LogoutToken.builder()
                .id(accessToken)
                .email(email)
                .expiration(remainingSec/1000)
                .build();
    }
}
