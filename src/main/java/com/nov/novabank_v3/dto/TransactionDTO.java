package com.nov.novabank_v3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class TransactionDTO {
    @NotNull
    private Long transaction_id;

    @NotBlank(message = "El tipo es obligatorio.")
    private String transaction_type;

    @NotNull(message = "El saldo es obligatorio.")
    @Positive(message = "El importe debe ser superior a 0.")
    private BigDecimal amount;

    @NotBlank(message = "El campo descripción es obligatorio.")
    private String description;

    @NotNull
    private LocalDateTime creation_date;

    @NotNull
    private Long account_id;

    public TransactionDTO(String transaction_type, BigDecimal amount, String description, LocalDateTime creation_date, Long account_id) {
        this.transaction_type = transaction_type;
        this.amount = amount;
        this.description = description;
        this.creation_date = creation_date;
        this.account_id = account_id;
    }
}