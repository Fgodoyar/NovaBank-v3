package com.nov.novabank_v3.repository;

import com.nov.novabank_v3.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(Long accountId, LocalDate startDate, LocalDate endDate);
}
