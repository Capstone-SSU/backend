package com.example.demo.user.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "companyDomains")
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyDomainId;

    @Column
    private String companyName;

    @Column
    private String companyDomain;
    //어떤 도메인이든 상관없이 메일 보낼 수 있는지 찾아보기
    //숭실대, 네이버는 테스트용으로 집어넣은 것

}
