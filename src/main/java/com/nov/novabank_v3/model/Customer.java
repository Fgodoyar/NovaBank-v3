package com.nov.novabank_v3.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "accounts")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customer_id;

    @Column
    private String customer_name;

    @Column
    private String lastname;

    @Column
    private String dni;

    @Column
    private String email;

    @Column
    private String phone_number;

    @Column
    private LocalDateTime creation_date;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    public Set<Account> accounts;

    @PrePersist
    public void prePersist() {
        this.creation_date = LocalDateTime.now();
    }

    public Customer(Long customer_id, String customer_name, String lastname, String dni, String email, String phone_number) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.lastname = lastname;
        this.dni = dni;
        this.email = email;
        this.phone_number = phone_number;
    }
}
