package com.paybills.paybills_api.application.controller;

import com.paybills.paybills_api.application.dto.bill.BillCreateRequestDTO;
import com.paybills.paybills_api.application.dto.bill.BillResponseDTO;
import com.paybills.paybills_api.application.dto.bill.BillUpdateRequestDTO;
import com.paybills.paybills_api.coredomain.service.BillService;
import com.paybills.paybills_api.infrastructure.enums.bill.BillStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/bills")
public class BillController {

    private final BillService service;

    @Autowired
    public BillController(BillService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BillResponseDTO> create(@Valid @RequestBody BillCreateRequestDTO billCreateRequestDTO) {
        BillResponseDTO response = BillResponseDTO.from(service.save(billCreateRequestDTO));
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillResponseDTO> update(@PathVariable String id, @Valid @RequestBody BillUpdateRequestDTO billUpdateRequestDTO) {
        BillResponseDTO response = BillResponseDTO.from(service.update(id, billUpdateRequestDTO));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable String id,
                                             @RequestParam BillStatus status,
                                             @RequestParam(required = false) LocalDate paymentDate) {
        service.updateStatus(id, status, paymentDate);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<BillResponseDTO>> getByFilters(@RequestParam(required = false) LocalDate dueDate,
                                                              @RequestParam(required = false) String description,
                                                              Pageable pageable) {
        Page<BillResponseDTO> response = service.getByFilters(dueDate, description, pageable)
                                                .map(BillResponseDTO::from);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponseDTO> getById(@PathVariable String id) {
        BillResponseDTO response = BillResponseDTO.from(service.getById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/total-paid")
    public ResponseEntity<BigDecimal> getTotalPaid(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        BigDecimal totalPaid = service.getTotalPaid(startDate, endDate);
        return ResponseEntity.ok(totalPaid);
    }
}

