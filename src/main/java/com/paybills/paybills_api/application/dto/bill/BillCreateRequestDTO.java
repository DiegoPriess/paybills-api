package com.paybills.paybills_api.application.dto.bill;

import com.paybills.paybills_api.infrastructure.enums.bill.BillStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BillCreateRequestDTO(
        @NotNull(message = "A data de vencimento é obrigatória")
        LocalDate dueDate,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor da fatura deve ser positivo")
        BigDecimal amount,

        @NotEmpty(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "O status é obrigatório")
        BillStatus status
) {
}
