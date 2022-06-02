package com.example.demo.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@Data
@RedisHash("refreshToken")
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    private String id; //id == nickname or email

    private String refreshToken;

    @TimeToLive
    private Long expiration;

    public static RefreshToken generateRefreshToken(String email, String refreshToken, Long expiration){
        return RefreshToken.builder()
                .id(email)
                .refreshToken(refreshToken)
                .expiration(expiration/1000)
                .build();
    }


}
