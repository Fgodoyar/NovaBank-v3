package com.nov.novabank_v3.model.factory;

import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.model.Customer;
import com.nov.novabank_v3.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionFactory {
    public static Transaction createDeposit(Account account, BigDecimal amount) {
        return new Transaction(
                "DEPÓSITO",
                amount,
                "Depósito de fondos",
                LocalDateTime.now(),
                account
        );

    }

    public static Transaction createWithdrawal(Account account, BigDecimal amount) {
        return new Transaction(
                "RETIRO",
                amount,
                "Retiro de fondos",
                LocalDateTime.now(),
                account
        );

    }

    public static Transaction createOutgoingTransfer(Account account, BigDecimal amount, String destination_account) {

        String description = "Transferencia enviada a " + destination_account;

        return new Transaction(
                "TRANSFERENCIA_SALIENTE",
                amount,
                description,
                LocalDateTime.now(),
                account
        );
    }

    public static Transaction createIncomingTransfer(Account account, BigDecimal amount, String source_account) {

        String description = "Transferencia recibida de " + source_account;

        return new Transaction(
                "TRANSFERENCIA_ENTRANTE",
                amount,
                description,
                LocalDateTime.now(),
                account
        );
    }
}
