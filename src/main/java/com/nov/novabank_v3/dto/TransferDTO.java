package com.nov.novabank_v3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferDTO {
    @NotNull(message = "El número de cuenta origen es obligatorio.")
    @Schema(description = "Cuenta que realiza la transacción")
    private String sourceAccount;

    @NotNull(message = "El número de cuenta destino es obligatorio.")
    @Schema(description = "Cuenta que recibe la transacción")
    private String destinationAccount;

    @NotNull(message = "El importe es obligatorio,")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor que 0.")
    @Schema(description = "Cantidad con la que se ha operado")
    private BigDecimal amount;
}
