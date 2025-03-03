package com.paybills.paybills_api.infrastructure.repository.user;

import com.paybills.paybills_api.coredomain.model.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, String> {
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bill b WHERE b.paymentDate BETWEEN :startDate AND :endDate AND b.user.id = :userId")
    BigDecimal getTotalPaidBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("userId") String userId);

    @Query("""
    SELECT b FROM Bill b
        WHERE (COALESCE(:dueDate, b.dueDate) = b.dueDate OR b.dueDate <= :dueDate)
        AND (COALESCE(:description, '') = '' OR LOWER(b.description) LIKE LOWER(CONCAT('%', :description, '%')))
        AND b.user.id = :userId
    """)
    Page<Bill> findByFilters(
            @Param("dueDate") LocalDate endDate,
            @Param("description") String description,
            @Param("userId") String userId,
            Pageable pageable
    );

    @Query("SELECT b FROM Bill b WHERE b.id = :id AND b.user.id = :userId")
    Optional<Bill> findByIdAndUser(@Param("id") String id, @Param("userId") String userId);
}

