package com.nov.novabank_v3.controller;

import com.nov.novabank_v3.dto.AccountDTO;
import com.nov.novabank_v3.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Gestión de cuentas bancarias de NovaBank")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Crear nueva cuenta", description = "Crea una cuenta bancaria asociada a un cliente existente.")
    @ApiResponse(responseCode = "201", description = "Cuenta creada correctamente")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(dto));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Listar cuentas por cliente", description = "Devuelve todas las cuentas asociadas a un cliente.")
    @ApiResponse(responseCode = "200", description = "Lista de cuentas obtenida")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<List<AccountDTO>> findByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.findByCustomerId(customerId));
    }

    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "Buscar cuenta por número", description = "Devuelve la cuenta correspondiente al número IBAN proporcionado.")
    @ApiResponse(responseCode = "200", description = "Cuenta encontrada")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    public ResponseEntity<AccountDTO> findByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.findByAccountNumber(accountNumber));
    }

    @GetMapping("/customer/{customerId}/transactions")
    @Operation(summary = "Cuentas con transacciones", description = "Devuelve las cuentas de un cliente incluyendo sus movimientos.")
    @ApiResponse(responseCode = "200", description = "Cuentas con movimientos obtenidas")
    public ResponseEntity<List<AccountDTO>> findByCustomerIdWithTransactions(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.findByCustomerIdWithTransactions(customerId));
    }
}