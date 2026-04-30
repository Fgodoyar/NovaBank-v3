package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.exception.InsufficientBalanceException;
import com.nov.novabank_v3.factory.TransactionFactory;
import com.nov.novabank_v3.mapper.TransactionMapper;
import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.model.Transaction;
import com.nov.novabank_v3.repository.AccountRepository;
import com.nov.novabank_v3.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;


    @Transactional
    @Override
    public TransactionDTO deposit(String accountNumber, BigDecimal balance) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("No se ha encontrado la cuenta con numero: " + accountNumber));

        if (balance.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("El monto tiene que ser mayor que 0.");
        }

        BigDecimal newBalance = account.getBalance().add(balance);
        account.setBalance(newBalance);
        accountRepository.save(account);

        TransactionDTO transactionDTO = TransactionFactory.createDeposit(account.getAccount_id(), balance);

        Transaction transaction = transactionMapper.toEntity(transactionDTO);

        transactionRepository.save(transaction);

        return transactionDTO;
    }

    @Transactional
    @Override
    public TransactionDTO withdraw(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("No se ha encontrado la cuenta con numero: " + accountNumber));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(account.getAccount_number(), account.getBalance(), amount);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("El monto tiene que ser mayor que 0.");
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        TransactionDTO transactionDTO = TransactionFactory.createWithdrawal(account.getAccount_id(), amount);

        Transaction transaction = transactionMapper.toEntity(transactionDTO);

        transactionRepository.save(transaction);

        return transactionDTO;
    }

    @Transactional
    @Override
    public List<TransactionDTO> transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        Account sourceAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la cuenta con numero: " + fromAccountNumber));

        if (sourceAccount.getAccount_number().equals(toAccountNumber)) {
            throw new RuntimeException("El origen no puede ser igual al destino");
        }

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(sourceAccount.getAccount_number(), sourceAccount.getBalance(), amount);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("El monto tiene que ser mayor que 0.");
        }

        BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(amount);
        sourceAccount.setBalance(newSourceBalance);
        accountRepository.save(sourceAccount);

        Account destinationAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la cuenta con numero: " + toAccountNumber));

        BigDecimal newDestinationBalance = destinationAccount.getBalance().add(amount);
        destinationAccount.setBalance(newDestinationBalance);
        accountRepository.save(destinationAccount);

        TransactionDTO incomingTransactionDTO = TransactionFactory.createIncomingTransfer(destinationAccount.getAccount_id(), amount, sourceAccount.getAccount_number());
        TransactionDTO outgoingTransactionDTO = TransactionFactory.createOutgoingTransfer(destinationAccount.getAccount_id(), amount, destinationAccount.getAccount_number());

        Transaction incomingTransaction = transactionMapper.toEntity(incomingTransactionDTO);
        Transaction outgoingTransaction = transactionMapper.toEntity(outgoingTransactionDTO);

        transactionRepository.save(outgoingTransaction);
        transactionRepository.save(incomingTransaction);

        return List.of(incomingTransactionDTO, outgoingTransactionDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> findByAccountId(Long accountId) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta con el ID: " + accountId));
        return transactionRepository.findByAccountId(account.getAccount_id()).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(Long accountId, LocalDate startDate, LocalDate endDate){
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta con el ID: " + accountId));
        return transactionRepository.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(account.getAccount_id(), startDate, endDate).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
