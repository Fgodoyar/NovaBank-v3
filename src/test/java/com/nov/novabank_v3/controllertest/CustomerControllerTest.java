package com.nov.novabank_v3.controllertest;

import com.nov.novabank_v3.config.SecurityConfig;
import com.nov.novabank_v3.controller.CustomerController;
import com.nov.novabank_v3.dto.CustomerDTO;
import com.nov.novabank_v3.exception.CustomerNotFoundException;
import com.nov.novabank_v3.service.CustomerService;
import com.nov.novabank_v3.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(SecurityConfig.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private CustomerDTO customerDTO;

    private static final String VALID_JSON = """
            {
              "customer_id": 1,
              "customer_name": "Juan Bartolomeo García",
              "last_name": "García",
              "dni": "12345678A",
              "email": "juanbartolitogarcia@email.com",
              "phone_number": "600123456",
              "creation_date": "2026-01-01T00:00:00",
              "numberOfAccounts": 0
            }
            """;

    @BeforeEach
    void setUp() {
        customerDTO = CustomerDTO.builder()
                .customer_id(1L)
                .customer_name("Juan Bartolomeo")
                .last_name("García")
                .dni("12345678A")
                .email("juanbartolitogarcia@email.com")
                .phone_number("600123456")
                .creation_date(LocalDateTime.of(2026, 1, 1, 0, 0))
                .numberOfAccounts(0)
                .build();
    }

    @Nested
    class createCustomerTest{

        @Test
        @WithMockUser
        @DisplayName("POST /api/customers → 201 con cliente creado")
        void createCustomer_shouldReturn201WhenValid() throws Exception {
            when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(customerDTO);

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.customer_id").value(1L))
                    .andExpect(jsonPath("$.dni").value("12345678A"));

            verify(customerService).createCustomer(any(CustomerDTO.class));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/customers → 400 si el body no supera la validación @Valid")
        void createCustomer_shouldReturn400WhenInvalid() throws Exception {
            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(customerService);
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/customers → 400 si DNI/email/teléfono ya están registrados")
        void createCustomer_shouldReturn400WhenDuplicate() throws Exception {
            when(customerService.createCustomer(any(CustomerDTO.class)))
                    .thenThrow(new IllegalArgumentException("DNI ya registrado"));

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/customers → 403 sin autenticación")
        void createCustomer_shouldReturn403WhenUnauthenticated() throws Exception {
            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_JSON))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(customerService);
        }
    }

    @Nested
    class listCustomersTest{

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers → 200 con lista de clientes")
        void listCustomers_shouldReturn200WithList() throws Exception {
            when(customerService.listCustomers()).thenReturn(List.of(customerDTO));

            mockMvc.perform(get("/api/customers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].customer_id").value(1L))
                    .andExpect(jsonPath("$[0].email").value("juanbartolitogarcia@email.com"));

            verify(customerService, times(1)).listCustomers();
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers → 200 con lista vacía")
        void listCustomers_shouldReturn200WithEmptyList() throws Exception {
            when(customerService.listCustomers()).thenReturn(List.of());

            mockMvc.perform(get("/api/customers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    class getCustomerByIdTest{

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers/{id} → 200 cuando el cliente existe")
        void findById_shouldReturn200WhenFound() throws Exception {
            when(customerService.findById(1L)).thenReturn(customerDTO);

            mockMvc.perform(get("/api/customers/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customer_id").value(1L))
                    .andExpect(jsonPath("$.customer_name").value("Juan Bartolomeo"));

            verify(customerService).findById(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers/{id} → 404 cuando el cliente no existe")
        void findById_shouldReturn404WhenNotFound() throws Exception {
            when(customerService.findById(99L))
                    .thenThrow(new CustomerNotFoundException("Cliente no encontrado"));

            mockMvc.perform(get("/api/customers/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class getCustomerByDniTest{

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers/dni/{dni} → 200 cuando el cliente existe")
        void findByDni_shouldReturn200WhenFound() throws Exception {
            when(customerService.findByDni("12345678A")).thenReturn(customerDTO);

            mockMvc.perform(get("/api/customers/dni/12345678A"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dni").value("12345678A"));

            verify(customerService).findByDni("12345678A");
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers/dni/{dni} → 404 cuando no existe")
        void findByDni_shouldReturn404WhenNotFound() throws Exception {
            when(customerService.findByDni("00000000X"))
                    .thenThrow(new CustomerNotFoundException("Cliente no encontrado"));

            mockMvc.perform(get("/api/customers/dni/00000000X"))
                    .andExpect(status().isNotFound());
        }
    }
}