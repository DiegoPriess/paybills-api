package com.paybills.paybills_api.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.paybills.paybills_api.application.dto.bill.BillCreateRequestDTO;
import com.paybills.paybills_api.application.dto.bill.BillUpdateRequestDTO;
import com.paybills.paybills_api.coredomain.model.Bill;
import com.paybills.paybills_api.coredomain.model.User;
import com.paybills.paybills_api.coredomain.service.AuthorizationService;
import com.paybills.paybills_api.coredomain.service.BillService;
import com.paybills.paybills_api.infrastructure.enums.bill.BillStatus;
import com.paybills.paybills_api.infrastructure.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private BillService billService;

    private Bill bill;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId("1");

        bill = Bill.builder()
                .id("1")
                .dueDate(LocalDate.now().plusDays(10))
                .amount(new BigDecimal("100.00"))
                .description("Test Bill")
                .status(BillStatus.PENDING)
                .user(mockUser)
                .build();
    }

    @Test
    void shouldSaveBillSuccessfully() {
        BillCreateRequestDTO request = new BillCreateRequestDTO(
                bill.getDueDate(),
                bill.getAmount(),
                bill.getDescription(),
                bill.getStatus()
        );

        when(authorizationService.getCurrentUser()).thenReturn(mockUser);
        when(billRepository.save(any(Bill.class))).thenReturn(bill);

        Bill savedBill = billService.save(request);

        assertNotNull(savedBill);
        assertEquals(bill.getAmount(), savedBill.getAmount());
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    @Test
    void shouldUpdateBillSuccessfully() {
        BillUpdateRequestDTO request = new BillUpdateRequestDTO(
                bill.getDueDate().plusDays(5),
                LocalDate.now(),
                new BigDecimal("200.00"),
                "Updated Bill"
        );

        lenient().when(authorizationService.getCurrentUser()).thenReturn(mockUser);

        when(billRepository.findById("1")).thenReturn(Optional.of(bill));
        when(billRepository.save(any(Bill.class))).thenReturn(bill);

        Bill updatedBill = billService.update("1", request);

        assertNotNull(updatedBill);
        assertEquals(request.amount(), updatedBill.getAmount());
        assertEquals(request.description(), updatedBill.getDescription());

        verify(billRepository, times(1)).save(any(Bill.class));
    }

    @Test
    void shouldUpdateBillStatusSuccessfully() {
        when(billRepository.findById("1")).thenReturn(Optional.of(bill));

        billService.updateStatus("1", BillStatus.PAID, LocalDate.now());

        assertEquals(BillStatus.PAID, bill.getStatus());
        assertNotNull(bill.getPaymentDate());
        verify(billRepository, times(1)).save(bill);
    }

    @Test
    void shouldGetBillsByFilters() {
        Page<Bill> page = new PageImpl<>(Collections.singletonList(bill));
        when(authorizationService.getCurrentUser()).thenReturn(mockUser);
        when(billRepository.findByFilters(any(), any(), any(), any())).thenReturn(page);

        Page<Bill> result = billService.getByFilters(null, null, Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldGetBillByIdSuccessfully() {
        when(authorizationService.getCurrentUser()).thenReturn(mockUser);
        when(billRepository.findByIdAndUser(any(), any())).thenReturn(Optional.of(bill));

        Bill result = billService.getById("1");

        assertNotNull(result);
        assertEquals(bill.getId(), result.getId());
    }

    @Test
    void shouldGetTotalPaidSuccessfully() {
        when(authorizationService.getCurrentUser()).thenReturn(mockUser);
        when(billRepository.getTotalPaidBetweenDates(any(), any(), any())).thenReturn(new BigDecimal("500.00"));

        BigDecimal totalPaid = billService.getTotalPaid(LocalDate.now().minusDays(10), LocalDate.now());

        assertEquals(new BigDecimal("500.00"), totalPaid);
    }

    @Test
    void shouldImportBillsSuccessfully() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(getClass().getResourceAsStream("/test-bills.csv"));

        billService.importBills(file);

        verify(billRepository, atLeastOnce()).save(any(Bill.class));
    }

    @Test
    void shouldThrowExceptionWhenImportingBillsFails() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Failed to read file"));

        assertThrows(RuntimeException.class, () -> billService.importBills(file));

        verify(billRepository, never()).save(any(Bill.class));
    }
}