package com.nov.novabank_v3.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO {
    @NotNull
    private Long customer_id;

    @NotBlank(message = "El nombre del cliente es obligatorio.")
    private String customer_name;

    @NotBlank(message = "Los apellidos del cliente son obligatorios.")
    private String lastname;

    @NotBlank(message = "El Documento de identificación es obligatorio.")
    @Size(max = 9, message = "El documento de identificación debe tener 9 caracteres.")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "Documento no válido")
    private String dni;

    @NotBlank(message = "El email es obligatorio.")
    @Email
    private String email;

    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Pattern(regexp = "^[0-9]{9}$", message = "El formato del teléfono no es válido")
    private String phone_number;

    @NotNull
    private LocalDateTime creation_date;

    @NotNull
    @PositiveOrZero
    private int numberOfAccounts;

}