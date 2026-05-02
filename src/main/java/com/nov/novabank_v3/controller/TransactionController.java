package com.nov.novabank_v3.controller;

import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Gestión de movimientos bancarios de NovaBank")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/transaction/{accountId}")
    @Operation(summary = "Listar movimientos por cuenta",
            description = "Devuelve todos los movimientos asociadas a una cuenta.")
    @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    public ResponseEntity<List<TransactionDTO>> findByAccountId(@PathVariable("accountId") Long accountId) {
        return ResponseEntity.ok(transactionService.findByAccountId(accountId));
    }


    @GetMapping("/account/{accountId}/dates/{startDate}/{endDate}")
    @Operation(summary = "Buscar transacciones por cuenta y rango de fechas",
            description = "Devuelve las transacciones de una cuenta entre dos fechas, ordenadas de más reciente a más antigua.")
    @ApiResponse(responseCode = "200", description = "Transacciones obtenidas correctamente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    public ResponseEntity<List<TransactionDTO>> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc
            (@PathVariable("accountId") Long accountId, @PathVariable("startDate") LocalDate startDate,
             @PathVariable("endDate") LocalDate endDate) {
        return ResponseEntity.ok(transactionService.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(accountId, startDate, endDate));
    }
}
