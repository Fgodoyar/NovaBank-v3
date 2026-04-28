package com.nov.novabank_v3.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long account_id;

    @NotNull
    @Column
    private String account_number;

    @NotNull
    @Column
    private String account_holder;

    @NotNull
    @Column
    private BigDecimal balance;

    @NotNull
    @Column
    private LocalDateTime creation_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Transaction> transactions;

    @PrePersist
    public void prePersist() {
        this.creation_date = LocalDateTime.now();
    }
}
