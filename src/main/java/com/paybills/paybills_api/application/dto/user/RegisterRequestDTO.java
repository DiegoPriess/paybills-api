package com.paybills.paybills_api.application.dto.user;

import com.paybills.paybills_api.infrastructure.enums.user.UserRole;

public record RegisterRequestDTO(String email, String password, UserRole role) {
}

