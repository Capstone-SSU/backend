package com.example.demo.security;

import com.example.demo.security.domain.LogoutToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoutTokenRepository extends CrudRepository<LogoutToken, String> {
}
