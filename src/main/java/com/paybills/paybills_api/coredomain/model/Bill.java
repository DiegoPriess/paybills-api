package com.paybills.paybills_api.coredomain.model;

import com.paybills.paybills_api.infrastructure.enums.bill.BillStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bills")
@EqualsAndHashCode(of = "id")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void validateForPersist() {
        if (this.amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("O valor da conta deve ser maior que zero");
        if (this.dueDate == null) throw new IllegalArgumentException("A data de vencimento é obrigatória!");
        if (this.description == null || this.description.isEmpty()) throw new IllegalArgumentException("A descrição é obrigatória!");
        if (this.status == null) throw new IllegalArgumentException("O status é obrigatório!");
    }

    public void validateStatusChange(BillStatus newStatus, LocalDate paymentDate) {
        if (newStatus == BillStatus.PAID && paymentDate == null) throw new IllegalArgumentException("É necessário informar a data de pagamento para uma conta paga");
        if (newStatus == BillStatus.PENDING && paymentDate != null) throw new IllegalArgumentException("Não é possível informar uma data de pagamento para uma conta pendente");
    }

}
