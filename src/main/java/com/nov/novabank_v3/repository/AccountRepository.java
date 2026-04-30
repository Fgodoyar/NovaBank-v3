package com.nov.novabank_v3.repository;

import com.nov.novabank_v3.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(Long accountId);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByCustomerId(Long customerId);

    // Carga cuentas con sus movimientos en una sola consulta (evita N+1)
    @Query("SELECT a FROM Accounts a LEFT JOIN FETCH a.transactions WHERE a.customers.customer_id = :customerId")
    List<Account> findByCustomerIdWithTransactions(Long customerId);
}
