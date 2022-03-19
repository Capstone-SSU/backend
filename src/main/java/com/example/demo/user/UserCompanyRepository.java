package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserCompanyRepository extends JpaRepository<Company,Long> {
    Company findByCompanyDomain(String domain);
}
