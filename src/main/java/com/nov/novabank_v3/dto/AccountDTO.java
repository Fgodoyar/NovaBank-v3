package com.nov.novabank_v3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {
    @NotNull
    private Long account_id;

    @NotBlank(message = "El número de cuenta es obligatorio.")
    @Size(min = 10, max = 34, message = "El número de cuenta debe tener entre 10 y 34 caracteres.")
    private String account_number;

    @NotBlank(message = "El nombre del titular es obligatorio.")
    private String account_holder;

    @NotNull(message = "El saldo es obligatorio.")
    @PositiveOrZero(message = "El sueldo no puede ser negativo.")
    private BigDecimal balance;

    @NotNull
    private LocalDateTime creation_date;

    @NotNull
    private Long customer_id;

    public AccountDTO(String account_number, String account_holder, BigDecimal balance, LocalDateTime creation_date, Long customer_id) {
        this.account_number = account_number;
        this.account_holder = account_holder;
        this.balance = balance;
        this.creation_date = creation_date;
        this.customer_id = customer_id;
    }
}
