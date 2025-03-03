package com.paybills.paybills_api.services;

import com.paybills.paybills_api.coredomain.model.User;
import com.paybills.paybills_api.coredomain.service.AuthorizationService;
import com.paybills.paybills_api.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthorizationService authorizationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("password123");
        ReflectionTestUtils.setField(authorizationService, "secret", "paybills_api");
    }

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        when(repository.findByEmail("test@gmail.com")).thenReturn(user);

        UserDetails userDetails = authorizationService.loadUserByUsername("test@gmail.com");

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        verify(repository, times(1)).findByEmail("test@gmail.com");
    }

    @Test
    void shouldGenerateTokenSuccessfully() {
        String token = authorizationService.generateToken(user);

        assertNotNull(token);
        assertTrue(token.startsWith("ey"));
    }

    @Test
    void shouldReturnEmptyStringWhenTokenIsInvalid() {
        String invalidToken = "123456789";

        String email = authorizationService.validateToken(invalidToken);

        assertEquals("", email);
    }

    @Test
    void shouldGetCurrentUserSuccessfully() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        User currentUser = authorizationService.getCurrentUser();

        assertNotNull(currentUser);
        assertEquals(user.getEmail(), currentUser.getEmail());
        verify(securityContext, times(1)).getAuthentication();
    }

    @Test
    void shouldThrowExceptionWhenCurrentUserIsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(NullPointerException.class, () -> {
            authorizationService.getCurrentUser();
        });
    }
}