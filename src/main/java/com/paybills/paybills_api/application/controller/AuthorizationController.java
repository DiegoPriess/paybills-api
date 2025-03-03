package com.paybills.paybills_api.application.controller;

import com.paybills.paybills_api.application.dto.user.AuthenticationRequestDTO;
import com.paybills.paybills_api.application.dto.user.AuthenticationResponseDTO;
import com.paybills.paybills_api.application.dto.user.RegisterRequestDTO;
import com.paybills.paybills_api.coredomain.model.User;
import com.paybills.paybills_api.coredomain.service.AuthorizationService;
import com.paybills.paybills_api.infrastructure.repository.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthorizationController {

    @Autowired
    AuthorizationController(AuthorizationService service, AuthenticationManager manager, UserRepository repository) {
        this.service = service;
        this.manager = manager;
        this.repository = repository;
    }

    private final AuthorizationService service;
    private final AuthenticationManager manager;
    private final UserRepository repository;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody @Valid AuthenticationRequestDTO requestDTO) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(requestDTO.email(), requestDTO.password());
        Authentication auth = this.manager.authenticate(usernamePassword);
        String token = service.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new AuthenticationResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestDTO requestDTO) {
        if (this.repository.findByEmail(requestDTO.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(requestDTO.password());
        User newUser = User.builder()
                .email(requestDTO.email())
                .password(encryptedPassword)
                .role(UserRole.USER)
                .build();

        repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
