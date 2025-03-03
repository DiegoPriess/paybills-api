package com.paybills.paybills_api.coredomain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.paybills.paybills_api.coredomain.model.User;
import com.paybills.paybills_api.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AuthorizationService implements UserDetailsService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Autowired
    public AuthorizationService(UserRepository repository) {
        this.repository = repository;
    }

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username);
    }

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                      .withIssuer("patbills_api")
                      .withSubject(user.getEmail())
                      .withExpiresAt(this.generateExpirationDate())
                      .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("AuthorizationService.generateToken: Erro ao gerar token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm).withIssuer("patbills_api").build().verify(token).getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}

