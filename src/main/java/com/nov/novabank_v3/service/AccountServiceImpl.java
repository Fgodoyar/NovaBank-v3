package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.AccountDTO;
import com.nov.novabank_v3.exception.AccountNotFoundException;
import com.nov.novabank_v3.exception.CustomerNotFoundException;
import com.nov.novabank_v3.mapper.AccountMapper;
import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.model.Customer;
import com.nov.novabank_v3.repository.AccountRepository;
import com.nov.novabank_v3.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    @Override
    public AccountDTO createAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + customerId));

        if (accountRepository.existsByCustomerCustomerId(customer.getCustomerId())) {
            throw new IllegalArgumentException("El cliente ya tiene una cuenta registrada");
        }

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountHolder(customer.getCustomerName())
                .customer(customer)
                .creationDate(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .build();

        Account saved = accountRepository.saveAndFlush(account);

        return AccountDTO.builder()
                .accountId(saved.getAccountId())
                .accountNumber(saved.getAccountNumber())
                .accountHolder(saved.getAccountHolder())
                .balance(saved.getBalance())
                .creationDate(saved.getCreationDate())
                .customerId(customer.getCustomerId())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountDTO> findByCustomerId(Long customerId) {
        return accountRepository.findByCustomerCustomerId(customerId).stream()
                .map(account -> AccountDTO.builder()
                        .accountId(account.getAccountId())
                        .accountNumber(account.getAccountNumber())
                        .accountHolder(account.getAccountHolder())
                        .balance(account.getBalance())
                        .creationDate(account.getCreationDate())
                        .customerId(account.getCustomer() != null ? account.getCustomer().getCustomerId() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public AccountDTO findByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return AccountDTO.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .accountHolder(account.getAccountHolder())
                .balance(account.getBalance())
                .creationDate(account.getCreationDate())
                .customerId(account.getCustomer() != null ? account.getCustomer().getCustomerId() : null)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountDTO> findByCustomerIdWithTransactions(Long customerId) {
        return accountRepository.findByCustomerIdWithTransactions(customerId).stream()
                .map(account -> AccountDTO.builder()
                        .accountId(account.getAccountId())
                        .accountNumber(account.getAccountNumber())
                        .accountHolder(account.getAccountHolder())
                        .balance(account.getBalance())
                        .creationDate(account.getCreationDate())
                        .customerId(account.getCustomer() != null ? account.getCustomer().getCustomerId() : null)
                        .build())
                .collect(Collectors.toList());
    }

    private String generateAccountNumber() {
        return "ES91210000" + String.format("%012d", new
                Random().nextLong(1_000_000_000_000L));
    }
}
