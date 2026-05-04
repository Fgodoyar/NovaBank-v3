package com.nov.novabank_v3.repository;

import com.nov.novabank_v3.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(Long accountId);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByCustomerCustomerId(Long customerId);
    boolean existsByCustomerCustomerId(Long customerId);

    // Carga cuentas con sus movimientos en una sola consulta (evita N+1)
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.transactions WHERE a.customer.customerId = :customerId")
    List<Account> findByCustomerIdWithTransactions(@Param("customerId") Long customerId);
}
