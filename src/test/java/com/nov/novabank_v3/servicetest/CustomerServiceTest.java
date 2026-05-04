package com.nov.novabank_v3.servicetest;

import com.nov.novabank_v3.dto.CustomerDTO;
import com.nov.novabank_v3.exception.CustomerNotFoundException;
import com.nov.novabank_v3.mapper.CustomerMapper;
import com.nov.novabank_v3.model.Customer;
import com.nov.novabank_v3.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Long customerId;
    private String name;
    private String lastName;
    private String dni;
    private String email;
    private String phone;
    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        name = "Pepillo";
        lastName = "Grillo";
        dni = "76543210A";
        email = "pepeergrillo@email.com";
        phone = "654789345";

        customer = Customer.builder()
                .customerId(customerId)
                .customer_name(name)
                .last_name(lastName)
                .dni(dni)
                .email(email)
                .phoneNumber(phone)
                .creationDate(LocalDateTime.now())
                .build();

        customerDTO = CustomerDTO.builder()
                .customer_id(customerId)
                .customer_name(name)
                .last_name(lastName)
                .dni(dni)
                .email(email)
                .phone_number(phone)
                .build();
    }

    @Nested
    class CreateCustomerTest {

        @Test
        void createCustomer_duplicateDni_shouldThrowException() {
            when(customerRepository.existsByDni(dni)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () ->
                    customerService.createCustomer(customerDTO)
            );
            verify(customerRepository, never()).save(any());
        }

        @Test
        void createCustomer_duplicateEmail_shouldThrowException() {
            when(customerRepository.existsByDni(dni)).thenReturn(false);
            when(customerRepository.existsByEmail(email)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () ->
                    customerService.createCustomer(customerDTO)
            );
            verify(customerRepository, never()).save(any());
        }

        @Test
        void createCustomer_duplicatePhone_shouldThrowException() {
            when(customerRepository.existsByDni(dni)).thenReturn(false);
            when(customerRepository.existsByEmail(email)).thenReturn(false);
            when(customerRepository.existsByPhoneNumber(phone)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () ->
                    customerService.createCustomer(customerDTO)
            );
            verify(customerRepository, never()).save(any());
        }

        @Test
        void createCustomer_validData_shouldSaveSuccessfully() {
            when(customerRepository.existsByDni(dni)).thenReturn(false);
            when(customerRepository.existsByEmail(email)).thenReturn(false);
            when(customerRepository.existsByPhoneNumber(phone)).thenReturn(false);
            when(customerMapper.toEntity(customerDTO)).thenReturn(customer);
            when(customerRepository.save(any())).thenReturn(customer);
            when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

            CustomerDTO result = customerService.createCustomer(customerDTO);

            assertEquals(customerDTO, result);
            verify(customerRepository).save(any());
        }
    }

    @Nested
    class FindByIdTest {

        @Test
        void findById_existingCustomer_shouldReturnCustomer() {
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

            CustomerDTO result = customerService.findById(customerId);

            assertEquals(customerDTO, result);
            verify(customerRepository).findById(customerId);
        }

        @Test
        void findById_nonExistingCustomer_shouldThrowException() {
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            assertThrows(CustomerNotFoundException.class,
                    () -> customerService.findById(customerId));

            verify(customerRepository).findById(customerId);
        }
    }

    @Nested
    class FindByDniTest {

        @Test
        void findByDni_existingCustomer_shouldReturnCustomer() {
            when(customerRepository.findByDni(dni)).thenReturn(Optional.of(customer));
            when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

            CustomerDTO result = customerService.findByDni(dni);

            assertEquals(customerDTO, result);
            verify(customerRepository).findByDni(dni);
        }

        @Test
        void findByDni_nonExistingCustomer_shouldThrowException() {
            when(customerRepository.findByDni(dni)).thenReturn(Optional.empty());

            assertThrows(CustomerNotFoundException.class,
                    () -> customerService.findByDni(dni));

            verify(customerRepository).findByDni(dni);
        }
    }

    @Nested
    class ListAllTest {

        @Test
        void listAll_shouldReturnCustomerList() {
            Customer customer2 = Customer.builder()
                    .customerId(2L)
                    .customer_name("Pepilla")
                    .last_name("Pili")
                    .dni("77654321L")
                    .email("pepilla54@gmail.com")
                    .phoneNumber("654789345")
                    .creationDate(LocalDateTime.now())
                    .build();

            when(customerRepository.findAll()).thenReturn(List.of(customer, customer2));
            when(customerMapper.toDTO(any())).thenReturn(customerDTO);

            List<CustomerDTO> result = customerService.listCustomers();

            assertEquals(2, result.size());
            verify(customerRepository).findAll();
        }
    }
}
