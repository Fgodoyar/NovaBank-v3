package com.nov.novabank_v3.repository;

import com.nov.novabank_v3.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByCreationDateDesc(Long accountId);
    List<Transaction> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}
