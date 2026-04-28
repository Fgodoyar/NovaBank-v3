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
@Entity
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transaction_id;

    @NotNull
    @Column
    private String transaction_type;

    @NotNull
    @Column
    private BigDecimal amount;

    @NotNull
    @Column
    private String description;

    @NotNull
    @Column
    private LocalDateTime creation_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public Transaction(String transaction_type, BigDecimal amount, String description, LocalDateTime creation_date, Account account) {
        this.transaction_type = transaction_type;
        this.amount = amount;
        this.description = description;
        this.creation_date = creation_date;
        this.account = account;
    }
}
