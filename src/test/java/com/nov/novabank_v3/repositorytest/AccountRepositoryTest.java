package com.nov.novabank_v3.repositorytest;

import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.model.Customer;
import com.nov.novabank_v3.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.sql.init.mode=never", "spring.jpa.hibernate.ddl-auto=create-drop"})
public class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private Customer customer;
    private Account account;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .customerName("Jonnas")
                .lastName("Domingo")
                .dni("12345678A")
                .email("jonnas@gmail.com")
                .phoneNumber("600000001")
                .build();
        entityManager.persist(customer);

        account = Account.builder()
                .accountNumber("ACC-001")
                .accountHolder("Jonnas Domingo")
                .balance(new BigDecimal("1000.00"))
                .customer(customer)
                .build();
        entityManager.persist(account);
        entityManager.flush();
    }

    @Nested
    class FindByAccountIdTest {

        @Test
        void shouldReturnAccountWhenExists() {
            Optional<Account> result = accountRepository.findByAccountId(account.getAccountId());
            assertThat(result).isPresent();
            assertThat(result.get().getAccountNumber()).isEqualTo("ACC-001");
        }

        @Test
        void shouldReturnEmptyWhenNotExists() {
            Optional<Account> result = accountRepository.findByAccountId(999L);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindByAccountNumberTest {

        @Test
        void shouldReturnAccountWhenNumberExists() {
            Optional<Account> result = accountRepository.findByAccountNumber("ACC-001");
            assertThat(result).isPresent();
            assertThat(result.get().getAccountHolder()).isEqualTo("Jonnas Domingo");
        }

        @Test
        void shouldReturnEmptyWhenNumberNotExists() {
            Optional<Account> result = accountRepository.findByAccountNumber("ACC-999");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindByCustomerCustomerIdTest {

        @Test
        void shouldReturnAccountsForCustomer() {
            List<Account> result = accountRepository.findByCustomerCustomerId(customer.getCustomerId());
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAccountNumber()).isEqualTo("ACC-001");
        }

        @Test
        void shouldReturnEmptyListWhenCustomerHasNoAccounts() {
            Customer otherCustomer = Customer.builder()
                    .customerName("Jacinta")
                    .lastName("Smit")
                    .dni("87654321B")
                    .email("jacinta@gmail.com")
                    .phoneNumber("600000002")
                    .build();
            entityManager.persistAndFlush(otherCustomer);

            List<Account> result = accountRepository.findByCustomerCustomerId(otherCustomer.getCustomerId());
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class ExistsByCustomerCustomerIdTest {

        @Test
        void shouldReturnTrueWhenCustomerHasAccount() {
            boolean exists = accountRepository.existsByCustomerCustomerId(customer.getCustomerId());
            assertThat(exists).isTrue();
        }

        @Test
        void shouldReturnFalseWhenCustomerHasNoAccount() {
            Customer otherCustomer = Customer.builder()
                    .customerName("Jacinta")
                    .lastName("Smit")
                    .dni("87654321B")
                    .email("jacinta@gmail.com")
                    .phoneNumber("600000002")
                    .build();
            entityManager.persistAndFlush(otherCustomer);

            boolean exists = accountRepository.existsByCustomerCustomerId(otherCustomer.getCustomerId());
            assertThat(exists).isFalse();
        }
    }

    @Nested
    class FindByCustomerIdWithTransactionsTest {

        @Test
        void shouldReturnAccountsWithTransactions() {
            List<Account> result = accountRepository.findByCustomerIdWithTransactions(customer.getCustomerId());
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAccountNumber()).isEqualTo("ACC-001");
        }

        @Test
        void shouldReturnEmptyWhenCustomerHasNoAccounts() {
            Customer otherCustomer = Customer.builder()
                    .customerName("Jacinta")
                    .lastName("Smit")
                    .dni("87654321B")
                    .email("jacinta@gmail.com")
                    .phoneNumber("600000002")
                    .build();
            entityManager.persistAndFlush(otherCustomer);

            List<Account> result = accountRepository.findByCustomerIdWithTransactions(otherCustomer.getCustomerId());
            assertThat(result).isEmpty();
        }
    }
}