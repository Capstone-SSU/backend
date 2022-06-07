package com.example.demo.security;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    Long userId;

    public AuthResponse(String accessToken, Long id, String refreshToken){
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;
        this.userId=id;
    }
}
