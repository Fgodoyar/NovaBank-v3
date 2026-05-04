package com.nov.novabank_v3.repositorytest;

import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.model.Customer;
import com.nov.novabank_v3.model.Transaction;
import com.nov.novabank_v3.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.sql.init.mode=never", "spring.jpa.hibernate.ddl-auto=create-drop"})
class TransactionRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account account;

    private static final LocalDateTime BASE_DATE =
            LocalDateTime.of(2025, 6, 15, 10, 0);

    @BeforeEach
    void setUp() {

        Customer customer = Customer.builder()
                .customer_name("Juan")
                .last_name("Perez")
                .dni("12345678A")
                .email("juan@gmail.com")
                .phoneNumber("600000000")
                .build();

        entityManager.persist(customer);

        account = Account.builder()
                .accountNumber("ACC-001")
                .accountHolder("Juan Perez")
                .customer(customer)
                .balance(BigDecimal.valueOf(1000))
                .build();

        entityManager.persist(account);

        entityManager.persist(Transaction.builder()
                .transactionType("DEPOSIT")
                .amount(BigDecimal.valueOf(500))
                .description("Old transaction")
                .creationDate(BASE_DATE.minusDays(10))
                .account(account)
                .build());

        entityManager.persist(Transaction.builder()
                .transactionType("WITHDRAW")
                .amount(BigDecimal.valueOf(200))
                .description("Middle transaction")
                .creationDate(BASE_DATE.minusDays(5))
                .account(account)
                .build());

        entityManager.persist(Transaction.builder()
                .transactionType("DEPOSIT")
                .amount(BigDecimal.valueOf(300))
                .description("Recent transaction")
                .creationDate(BASE_DATE)
                .account(account)
                .build());

        entityManager.flush();
    }

    @Nested
    @DisplayName("findByAccount_AccountId")
    class FindByAccountId {

        @Test
        @DisplayName("devuelve todas las transacciones de la cuenta")
        void returnsTransactionsForAccount() {

            List<Transaction> result =
                    transactionRepository.findByAccount_AccountId(account.getAccountId());

            assertThat(result).hasSize(3);
            assertThat(result)
                    .allMatch(t -> t.getAccount().getAccountId().equals(account.getAccountId()));
        }

        @Test
        @DisplayName("devuelve vacío si la cuenta no existe")
        void returnsEmptyWhenAccountDoesNotExist() {

            List<Transaction> result =
                    transactionRepository.findByAccount_AccountId(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc")
    class FindByAccountIdAndDateRange {

        @Test
        @DisplayName("devuelve transacciones dentro del rango ordenadas DESC")
        void returnsTransactionsInRangeOrderedDesc() {

            LocalDateTime from = BASE_DATE.minusDays(7);
            LocalDateTime to = BASE_DATE;

            List<Transaction> result =
                    transactionRepository
                            .findByAccount_AccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                                    account.getAccountId(),
                                    from,
                                    to
                            );

            assertThat(result).hasSize(2);

            // orden DESC por fecha
            assertThat(result.get(0).getCreationDate())
                    .isAfterOrEqualTo(result.get(1).getCreationDate());
        }

        @Test
        @DisplayName("devuelve lista vacía si no hay datos en el rango")
        void returnsEmptyWhenNoDataInRange() {

            LocalDateTime from = BASE_DATE.minusDays(30);
            LocalDateTime to = BASE_DATE.minusDays(20);

            List<Transaction> result =
                    transactionRepository
                            .findByAccount_AccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                                    account.getAccountId(),
                                    from,
                                    to
                            );

            assertThat(result).isEmpty();
        }
    }
}