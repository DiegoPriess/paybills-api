package com.paybills.paybills_api.application.dto.bill;

import com.paybills.paybills_api.coredomain.model.Bill;
import com.paybills.paybills_api.infrastructure.enums.bill.BillStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BillResponseDTO(
        String id,
        LocalDate dueDate,
        LocalDate paymentDate,
        BigDecimal amount,
        String description,
        BillStatus status,
        String userId
) {
    public static BillResponseDTO from(Bill bill) {
        return new BillResponseDTO(
                bill.getId(),
                bill.getDueDate(),
                bill.getPaymentDate(),
                bill.getAmount(),
                bill.getDescription(),
                bill.getStatus(),
                bill.getUser().getId()
        );
    }
}
