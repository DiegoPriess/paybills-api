package com.paybills.paybills_api.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequestDTO(

        @NotNull(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        String email,

        @NotNull(message = "A senha é obrigatória")
        String password
) {
}

