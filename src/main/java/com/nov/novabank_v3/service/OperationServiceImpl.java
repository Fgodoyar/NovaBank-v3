package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.exception.InsufficientBalanceException;
import com.nov.novabank_v3.factory.TransactionFactory;
import com.nov.novabank_v3.mapper.TransactionMapper;
import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.repository.AccountRepository;
import com.nov.novabank_v3.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    @Override
    public TransactionDTO deposit(String accountNumber, BigDecimal amount) {
        Account account = findAccountByNumber(accountNumber);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto tiene que ser mayor que 0.");
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        TransactionDTO transactionDTO = TransactionFactory.createDeposit(account.getAccountId(), amount);
        transactionRepository.save(transactionMapper.toEntity(transactionDTO));

        return transactionDTO;
    }

    @Transactional
    @Override
    public TransactionDTO withdraw(String accountNumber, BigDecimal amount) {
        Account account = findAccountByNumber(accountNumber);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto tiene que ser mayor que 0.");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(account.getAccountNumber(), account.getBalance(), amount);
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        TransactionDTO transactionDTO = TransactionFactory.createWithdrawal(account.getAccountId(), amount);
        transactionRepository.save(transactionMapper.toEntity(transactionDTO));

        return transactionDTO;
    }

    @Transactional
    @Override
    public List<TransactionDTO> transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        Account sourceAccount = findAccountByNumber(fromAccountNumber);
        Account destinationAccount = findAccountByNumber(toAccountNumber);

        if (sourceAccount.getAccountNumber().equals(toAccountNumber)) {
            throw new IllegalArgumentException("La cuenta origen no puede ser igual a la cuenta destino.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto tiene que ser mayor que 0.");
        }

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(sourceAccount.getAccountNumber(), sourceAccount.getBalance(), amount);
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        accountRepository.save(sourceAccount);

        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));
        accountRepository.save(destinationAccount);

        TransactionDTO outgoingDTO = TransactionFactory.createOutgoingTransfer(
                sourceAccount.getAccountId(), amount, destinationAccount.getAccountNumber());

        TransactionDTO incomingDTO = TransactionFactory.createIncomingTransfer(
                destinationAccount.getAccountId(), amount, sourceAccount.getAccountNumber());

        transactionRepository.save(transactionMapper.toEntity(outgoingDTO));
        transactionRepository.save(transactionMapper.toEntity(incomingDTO));

        return List.of(outgoingDTO, incomingDTO);
    }

    private Account findAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se ha encontrado la cuenta con número: " + accountNumber));
    }
}