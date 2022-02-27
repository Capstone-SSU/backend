package com.example.demo.security;

import lombok.Data;

@Data
public class AuthResponse {
    private String jwtToken;
    Long userId;

    public AuthResponse(String jwtToken, Long id){
        this.jwtToken=jwtToken;
        this.userId=id;
    }
}
