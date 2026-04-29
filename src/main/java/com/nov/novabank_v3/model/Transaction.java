package com.nov.novabank_v3.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "account")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transaction_id;

    @Column
    private String transaction_type;

    @Column
    private BigDecimal amount;

    @Column
    private String description;

    @Column
    private LocalDateTime creation_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public Transaction(Long transaction_id, String transaction_type, BigDecimal amount, String description) {
        this.transaction_id = transaction_id;
        this.transaction_type = transaction_type;
        this.amount = amount;
        this.description = description;
        this.creation_date = LocalDateTime.now();
    }
}
