package com.paybills.paybills_api.application.controller;

import com.paybills.paybills_api.application.dto.bill.BillCreateRequestDTO;
import com.paybills.paybills_api.application.dto.bill.BillResponseDTO;
import com.paybills.paybills_api.application.dto.bill.BillUpdateRequestDTO;
import com.paybills.paybills_api.coredomain.service.BillService;
import com.paybills.paybills_api.infrastructure.enums.bill.BillStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Criar Conta", description = "Cria uma nova conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<BillResponseDTO> create(@Valid @RequestBody BillCreateRequestDTO billCreateRequestDTO) {
        BillResponseDTO response = BillResponseDTO.from(service.save(billCreateRequestDTO));
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Atualizar Conta", description = "Atualiza uma conta existente pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BillResponseDTO> update(@PathVariable String id, @Valid @RequestBody BillUpdateRequestDTO billUpdateRequestDTO) {
        BillResponseDTO response = BillResponseDTO.from(service.update(id, billUpdateRequestDTO));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar Status da Conta", description = "Atualiza o status de uma conta")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable String id,
                                             @RequestParam BillStatus status,
                                             @RequestParam(required = false) LocalDate paymentDate) {
        service.updateStatus(id, status, paymentDate);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Consultar Contas por Filtros", description = "Obtém contas filtradas com paginação")
    @GetMapping
    public ResponseEntity<Page<BillResponseDTO>> getByFilters(@RequestParam(required = false) LocalDate dueDate,
                                                              @RequestParam(required = false) String description,
                                                              Pageable pageable) {
        Page<BillResponseDTO> response = service.getByFilters(dueDate, description, pageable)
                                                .map(BillResponseDTO::from);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar Conta por ID", description = "Obtém uma conta pelo seu ID")
    @GetMapping("/{id}")
    public ResponseEntity<BillResponseDTO> getById(@PathVariable String id) {
        BillResponseDTO response = BillResponseDTO.from(service.getById(id));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar Total Pago", description = "Obtém o valor total pago dentro de um intervalo de datas")
    @GetMapping("/total-paid")
    public ResponseEntity<BigDecimal> getTotalPaid(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        BigDecimal totalPaid = service.getTotalPaid(startDate, endDate);
        return ResponseEntity.ok(totalPaid);
    }
}
