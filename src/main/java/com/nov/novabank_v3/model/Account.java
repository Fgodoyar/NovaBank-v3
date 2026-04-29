package com.nov.novabank_v3.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"customer", "transactions"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long account_id;

    @Column
    private String account_number;

    @Column
    private String account_holder;

    @Column
    private BigDecimal balance;

    @Column
    private LocalDateTime creation_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions;

    public Account(Long account_id, String account_number, String account_holder, BigDecimal balance) {
        this.account_id = account_id;
        this.account_number = account_number;
        this.account_holder = account_holder;
        this.balance = balance;
        this.creation_date = LocalDateTime.now();
    }
}
