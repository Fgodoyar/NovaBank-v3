package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.AccountDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {
    AccountDTO createAccount(AccountDTO dto);
    List<AccountDTO> findByCustomerId(Long customerId);
    AccountDTO findByAccountNumber(String accountNumber);
    List<AccountDTO> findByCustomerIdWithTransactions(Long customerId);
}
