package com.nov.novabank_v3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Número de la cuenta", example = "ES9121000000000000000002")
    private String account_number;

    @NotBlank(message = "El nombre del titular es obligatorio.")
    @Schema(description = "Nombre del titular de la cuenta", example = "Carlos")
    private String account_holder;

    @NotNull(message = "El saldo es obligatorio.")
    @PositiveOrZero(message = "El sueldo no puede ser negativo.")
    @Schema(description = "Saldo disponible de la cuenta")
    private BigDecimal balance;

    @NotNull
    @Schema(description = "Fecha de creación de la cuenta", example = "2026-05-18")
    private LocalDateTime creation_date;

    @NotNull
    @Schema(description = "ID del cliente vinculado a la cuenta")
    private Long customer_id;

    public AccountDTO(String account_number, String account_holder, BigDecimal balance, LocalDateTime creation_date, Long customer_id) {
        this.account_number = account_number;
        this.account_holder = account_holder;
        this.balance = balance;
        this.creation_date = creation_date;
        this.customer_id = customer_id;
    }
}
