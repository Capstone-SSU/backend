package com.example.demo.user.repository;

import com.example.demo.user.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserCompanyRepository extends JpaRepository<Company,Long> {
    Company findByCompanyDomain(String domain);
}
