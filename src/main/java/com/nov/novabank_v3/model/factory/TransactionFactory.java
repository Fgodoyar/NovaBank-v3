package com.nov.novabank_v3.model.factory;

import com.nov.novabank_v3.dto.TransactionDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionFactory {
    public static TransactionDTO createDeposit(Long account_id, BigDecimal amount) {
        return new TransactionDTO(
                "DEPÓSITO",
                amount,
                "Depósito de fondos",
                LocalDateTime.now(),
                account_id
        );

    }

    public static TransactionDTO createWithdrawal(Long account_id, BigDecimal amount) {
        return new TransactionDTO(
                "RETIRO",
                amount,
                "Retiro de fondos",
                LocalDateTime.now(),
                account_id
        );

    }

    public static TransactionDTO createOutgoingTransfer(Long account_id, BigDecimal amount, String destination_account) {

        String description = "Transferencia enviada a " + destination_account;

        return new TransactionDTO(
                "TRANSFERENCIA_SALIENTE",
                amount,
                description,
                LocalDateTime.now(),
                account_id
        );
    }

    public static TransactionDTO createIncomingTransfer(Long account_id, BigDecimal amount, String source_account) {

        String description = "Transferencia recibida de " + source_account;

        return new TransactionDTO(
                "TRANSFERENCIA_ENTRANTE",
                amount,
                description,
                LocalDateTime.now(),
                account_id
        );
    }
}
