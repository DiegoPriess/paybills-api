package com.paybills.paybills_api.application.controller;

import com.paybills.paybills_api.application.dto.user.AuthenticationRequestDTO;
import com.paybills.paybills_api.application.dto.user.AuthenticationResponseDTO;
import com.paybills.paybills_api.application.dto.user.RegisterRequestDTO;
import com.paybills.paybills_api.infrastructure.enums.user.UserRole;
import com.paybills.paybills_api.coredomain.model.User;
import com.paybills.paybills_api.coredomain.service.AuthorizationService;
import com.paybills.paybills_api.infrastructure.repository.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Login", description = "Autentica o usuário e retorna um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@Valid @RequestBody AuthenticationRequestDTO requestDTO) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(requestDTO.email(), requestDTO.password());
        Authentication auth = this.manager.authenticate(usernamePassword);
        String token = service.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new AuthenticationResponseDTO(token));
    }

    @Operation(summary = "Registrar", description = "Registra um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email já em uso")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
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
