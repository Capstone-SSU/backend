package com.example.demo.dto;

import com.example.demo.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AuthResponse {
    private String jwtToken;
    UserOnlyDto user;

    public AuthResponse(String jwtToken, UserOnlyDto user) {
        this.jwtToken=jwtToken;
        this.user=user;
    }
}
