package com.paybills.paybills_api.coredomain.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.paybills.paybills_api.application.dto.bill.BillCreateRequestDTO;
import com.paybills.paybills_api.application.dto.bill.BillUpdateRequestDTO;
import com.paybills.paybills_api.coredomain.model.Bill;
import com.paybills.paybills_api.infrastructure.enums.bill.BillStatus;
import com.paybills.paybills_api.infrastructure.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;

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
        bill.validateForPersist();
        return repository.save(bill);
    }

    public Bill update(String id, BillUpdateRequestDTO billUpdateRequestDTO) {
        Bill updatedBill = getBillById(id);

        updatedBill.setDueDate(billUpdateRequestDTO.dueDate());
        updatedBill.setPaymentDate(billUpdateRequestDTO.paymentDate());
        updatedBill.setAmount(billUpdateRequestDTO.amount());
        updatedBill.setDescription(billUpdateRequestDTO.description());

        updatedBill.validateForPersist();
        return repository.save(updatedBill);
    }

    public void updateStatus(String id, BillStatus status, LocalDate paymentDate) {
        Bill bill = getBillById(id);
        bill.validateStatusChange(status, paymentDate);
        bill.setStatus(status);
        bill.setPaymentDate(paymentDate);
        repository.save(bill);
    }

    public Page<Bill> getByFilters(LocalDate dueDate, String description, Pageable pageable) {
        String userId = authorizationService.getCurrentUser().getId();
        return repository.findByFilters(dueDate, description, userId, pageable);
    }

    public Bill getById(String id) {
        return getBillById(id);
    }

    public BigDecimal getTotalPaid(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) throw new IllegalArgumentException("A data de início não pode ser maior que a data de término");

        String userId = authorizationService.getCurrentUser().getId();
        return repository.getTotalPaidBetweenDates(startDate, endDate, userId);
    }

    public void importBills(MultipartFile file) {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            csvReader.readNext();
            while ((values = csvReader.readNext()) != null) {
                Bill bill = Bill.builder()
                        .dueDate(LocalDate.parse(values[0]))
                        .amount(new BigDecimal(values[1]))
                        .description(values[2])
                        .status(BillStatus.valueOf(values[3].toUpperCase()))
                        .user(authorizationService.getCurrentUser())
                        .build();
                bill.validateForPersist();
                repository.save(bill);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Erro ao processar o arquivo CSV", e);
        }
    }

    private Bill getBillById(String id) {
        String userId = authorizationService.getCurrentUser().getId();
        return repository.findByIdAndUser(id, userId)
                         .orElseThrow(() -> new RuntimeException("Conta não encontrada ou não pertence ao usuário"));
    }
}
