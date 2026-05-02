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
public class OperationDTO {
    @NotNull(message = "El número de cuenta es obligatorio.")
    @Schema(description = "Número de la cuenta", example = "ES9121000000000000000002")
    private String accountNumber;

    @NotNull(message = "El importe es obligatorio,")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor que 0.")
    @Schema(description = "Cantidad a introducir o retirar")
    private BigDecimal amount;

}
