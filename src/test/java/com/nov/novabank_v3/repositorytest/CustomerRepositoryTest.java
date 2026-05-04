package com.nov.novabank_v3.repositorytest;

import com.nov.novabank_v3.model.Customer;
import com.nov.novabank_v3.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {"spring.sql.init.mode=never", "spring.jpa.hibernate.ddl-auto=create-drop"})
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .customer_name("Pepillo")
                .last_name("Grillo")
                .dni("76543210A")
                .email("pepeergrillo@email.com")
                .phoneNumber("654789345")
                .build();

        customerRepository.save(customer);
    }

    @Nested
    class FindByDniTest {

        @Test
        void findByDni_existingDni_shouldReturnCustomer() {
            Optional<Customer> result = customerRepository.findByDni("76543210A");

            assertTrue(result.isPresent());
            assertEquals("76543210A", result.get().getDni());
        }

        @Test
        void findByDni_nonExistingDni_shouldReturnEmpty() {
            Optional<Customer> result = customerRepository.findByDni("00000000X");

            assertFalse(result.isPresent());
        }
    }

    @Nested
    class ExistsByDniTest {

        @Test
        void existsByDni_existingDni_shouldReturnTrue() {
            assertTrue(customerRepository.existsByDni("76543210A"));
        }

        @Test
        void existsByDni_nonExistingDni_shouldReturnFalse() {
            assertFalse(customerRepository.existsByDni("00000000X"));
        }
    }

    @Nested
    class ExistsByEmailTest {

        @Test
        void existsByEmail_existingEmail_shouldReturnTrue() {
            assertTrue(customerRepository.existsByEmail("pepeergrillo@email.com"));
        }

        @Test
        void existsByEmail_nonExistingEmail_shouldReturnFalse() {
            assertFalse(customerRepository.existsByEmail("noexiste@email.com"));
        }
    }

    @Nested
    class ExistsByPhoneNumberTest {

        @Test
        void existsByPhoneNumber_existingPhone_shouldReturnTrue() {
            assertTrue(customerRepository.existsByPhoneNumber("654789345"));
        }

        @Test
        void existsByPhoneNumber_nonExistingPhone_shouldReturnFalse() {
            assertFalse(customerRepository.existsByPhoneNumber("000000000"));
        }
    }

    @Nested
    class SaveTest {

        @Test
        void save_shouldPersistCustomer() {
            Customer newCustomer = Customer.builder()
                    .customer_name("Pepilla")
                    .last_name("Grilla")
                    .dni("12345678B")
                    .email("pepilla@email.com")
                    .phoneNumber("600000001")
                    .build();

            Customer saved = customerRepository.save(newCustomer);

            assertNotNull(saved.getCustomerId());
            assertEquals("12345678B", saved.getDni());
        }

        @Test
        void save_shouldSetCreationDateAutomatically() {
            Customer newCustomer = Customer.builder()
                    .customer_name("Pepilla")
                    .last_name("Grilla")
                    .dni("12345678B")
                    .email("pepilla@email.com")
                    .phoneNumber("600000001")
                    .build();

            Customer saved = customerRepository.save(newCustomer);

            assertNotNull(saved.getCreationDate());
        }
    }
}