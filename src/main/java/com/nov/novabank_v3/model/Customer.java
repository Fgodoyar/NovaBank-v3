package com.nov.novabank_v3.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customer_id;

    @NotNull
    @Column
    private String customer_name;

    @NotNull
    @Column
    private String last_name;

    @NotNull
    @Column
    private String dni;

    @NotNull
    @Column
    @Email
    private String email;

    @NotNull
    @Column
    private String phone_number;

    @NotNull
    @Column
    private LocalDateTime creation_date;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    public Set<Account> accounts;

    @PrePersist
    public void prePersist() {
        this.creation_date = LocalDateTime.now();
    }
}
