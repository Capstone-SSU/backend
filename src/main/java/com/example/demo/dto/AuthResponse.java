package com.example.demo.dto;

import com.example.demo.domain.User;
import lombok.AllArgsConstructor;
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
