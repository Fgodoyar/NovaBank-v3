package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.CustomerDTO;
import com.nov.novabank_v3.exception.CustomerNotFoundException;
import com.nov.novabank_v3.mapper.CustomerMapper;
import com.nov.novabank_v3.model.Customer;
import com.nov.novabank_v3.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerDTO findById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + customerId));
        return customerMapper.toDTO(customer);
    }

    @Transactional
    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {

        if (customerRepository.existsByDni(dto.getDni()))
            throw new IllegalArgumentException("Ya existe un cliente con este DNI: " + dto.getDni());

        if (customerRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Ya existe un cliente con este email: " + dto.getEmail());

        if (customerRepository.existsByPhoneNumber(dto.getPhone_number()))
            throw new IllegalArgumentException("Ya existe un cliente con este número de teléfono: " + dto.getPhone_number());

        Customer saved = customerRepository.save(customerMapper.toEntity(dto));
        return customerMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerDTO findByDni(String dni) {
        Customer customer = customerRepository.findByDni(dni)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + dni));
        return customerMapper.toDTO(customer);
    }

}