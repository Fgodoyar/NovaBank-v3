package com.nov.novabank_v3.controller;

import com.nov.novabank_v3.dto.OperationDTO;
import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.dto.TransferDTO;
import com.nov.novabank_v3.service.OperationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDTO> deposit(@Valid @RequestBody OperationDTO dto) {
        return ResponseEntity.ok(operationService.deposit(dto.getAccountNumber(), dto.getAmount()));
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<TransactionDTO> withdrawal(@Valid @RequestBody OperationDTO dto) {
        return ResponseEntity.ok(operationService.withdraw(dto.getAccountNumber(), dto.getAmount()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<List<TransactionDTO>> transfer(@Valid @RequestBody TransferDTO dto) {
        return ResponseEntity.ok(operationService.transfer(
                dto.getSourceAccount(),
                dto.getDestinationAccount(),
                dto.getAmount()
        ));
    }
}