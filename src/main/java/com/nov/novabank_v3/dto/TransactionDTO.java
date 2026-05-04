package com.nov.novabank_v3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "ID del movimiento")
    private Long transactionId;

    @Schema(description = "Tipo de movimiento", example = "transaccion_entrante, transacción_saliente...")
    @NotBlank(message = "El tipo es obligatorio.")
    private String transactionType;

    @NotNull(message = "El saldo es obligatorio.")
    @Positive(message = "El importe debe ser superior a 0.")
    @Schema(description = "Cantidad con la que se ha operado")
    private BigDecimal amount;

    @NotBlank(message = "El campo descripción es obligatorio.")
    @Schema(description = "Descripción del movimiento.")
    private String description;

    @NotNull
    @Schema(description = "Fecha de creación del movimiento", example = "2026-06-23")
    private LocalDateTime creationDate;

    @NotNull
    @Schema(description = "ID de la cuenta vinculada al movimiento")
    private Long accountId;

    public TransactionDTO(String transactionType, BigDecimal amount, String description, LocalDateTime creationDate, Long accountId) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.creationDate = creationDate;
        this.accountId = accountId;
    }
}