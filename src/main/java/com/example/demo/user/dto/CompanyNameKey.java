package com.example.demo.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CompanyNameKey {
    private Integer randomKey;
    private String companyName;

    public CompanyNameKey(Integer randomKey, String companyName) {
        this.randomKey = randomKey;
        this.companyName = companyName;
    }
}
