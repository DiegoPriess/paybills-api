package com.paybills.paybills_api.coredomain.service;

import com.paybills.paybills_api.application.dto.bill.BillCreateRequestDTO;
import com.paybills.paybills_api.application.dto.bill.BillUpdateRequestDTO;
import com.paybills.paybills_api.coredomain.model.Bill;
import com.paybills.paybills_api.infrastructure.enums.bill.BillStatus;
import com.paybills.paybills_api.infrastructure.repository.user.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class BillService {

    @Autowired
    public BillService(AuthorizationService authorizationService, BillRepository repository) {
        this.authorizationService = authorizationService;
        this.repository = repository;
    }

    private final AuthorizationService authorizationService;
    private final BillRepository repository;

    public Bill save(BillCreateRequestDTO billCreateRequestDTO) {
        Bill bill = Bill.builder()
                .dueDate(billCreateRequestDTO.dueDate())
                .amount(billCreateRequestDTO.amount())
                .description(billCreateRequestDTO.description())
                .status(billCreateRequestDTO.status())
                .user(authorizationService.getCurrentUser())
                .build();
        return repository.save(bill);
    }

    public Bill update(String id, BillUpdateRequestDTO billUpdateRequestDTO) {
        Optional<Bill> existingBill = repository.findById(id);
        if (existingBill.isEmpty()) throw new RuntimeException("Conta não encontrada");

        Bill updatedBill = existingBill.get();
        updatedBill.setDueDate(billUpdateRequestDTO.dueDate());
        updatedBill.setPaymentDate(billUpdateRequestDTO.paymentDate());
        updatedBill.setAmount(billUpdateRequestDTO.amount());
        updatedBill.setDescription(billUpdateRequestDTO.description());

        return repository.save(updatedBill);
    }

    public void updateStatus(String id, BillStatus status, LocalDate paymentDate) {
        Bill bill = repository.findById(id).orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        validateStatusChange(status, paymentDate);

        if (status == BillStatus.PENDING) {
            bill.setPaymentDate(null);
        } else if (status == BillStatus.PAID) {
            bill.setPaymentDate(paymentDate);
        }

        bill.setStatus(status);
        repository.save(bill);
    }

    public Page<Bill> getByFilters(LocalDate dueDate, String description, Pageable pageable) {
        String userId = authorizationService.getCurrentUser().getId();
        return repository.findByFilters(dueDate, description, userId, pageable);
    }

    public Bill getById(String id) {
        String userId = authorizationService.getCurrentUser().getId();
        return repository.findByIdAndUser(id, userId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada ou não pertence ao usuário"));
    }

    public BigDecimal getTotalPaid(LocalDate startDate, LocalDate endDate) {
        validateStartDateBeforeEndDate(startDate, endDate);

        String userId = authorizationService.getCurrentUser().getId();
        return repository.getTotalPaidBetweenDates(startDate, endDate, userId);
    }

    private void validateStatusChange(BillStatus status, LocalDate paymentDate) {
        if (status == BillStatus.PAID && paymentDate == null) {
            throw new RuntimeException("Ao pagar uma conta, é obrigatório informar a data de pagamento");
        }

        if (status == BillStatus.PENDING && paymentDate != null) {
            throw new RuntimeException("Não é possível informar uma data de pagamento para uma conta pendente");
        }
    }

    private void validateStartDateBeforeEndDate(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new RuntimeException("A data de início não pode ser maior que a data de término");
        }
    }
}
