package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.AccountDTO;
import com.nov.novabank_v3.exception.CustomerNotFoundException;
import com.nov.novabank_v3.mapper.AccountMapper;
import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.model.Customer;
import com.nov.novabank_v3.repository.AccountRepository;
import com.nov.novabank_v3.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountMapper accountMapper;

    @Transactional
    @Override
    public AccountDTO createAccount(AccountDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomer_id())
                .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + dto.getCustomer_id()));
        Account saved = accountRepository.save(accountMapper.toEntity(dto));
        return accountMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountDTO> findByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public AccountDTO findByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("No se ha encontrado la cuenta con número: " + accountNumber));
        return accountMapper.toDTO(account);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountDTO> findByCustomerIdWithTransactions(Long customerId) {
        return accountRepository.findByCustomerIdWithTransactions(customerId).stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    private String generateAccountNumber() {
        return "ES91210000" + String.format("%012d", new
                Random().nextLong(1_000_000_000_000L));
    }
}
