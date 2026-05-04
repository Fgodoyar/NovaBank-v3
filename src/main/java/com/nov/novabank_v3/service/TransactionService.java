package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.TransactionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TransactionService {
    List<TransactionDTO> findByAccountId(Long accountId);
    List<TransactionDTO> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}
