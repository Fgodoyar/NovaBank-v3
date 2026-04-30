package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.CustomerDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerService {
    List<CustomerDTO> listCustomers();
    CustomerDTO getCustomer(Long id);
    CustomerDTO createCustomer(CustomerDTO dto);
    CustomerDTO findByDni(String dni);
    CustomerDTO findByEmail(String email);
}
