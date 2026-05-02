package com.nov.novabank_v3.controller;

import com.nov.novabank_v3.dto.CustomerDTO;
import com.nov.novabank_v3.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Gestión de clientes de NovaBank")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Devuelve todos los clientes registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida")
    public ResponseEntity<List<CustomerDTO>> listCustomers() {
        return ResponseEntity.ok(customerService.listCustomers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<CustomerDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear cliente",
            description = "Registra un nuevo cliente. DNI, email y teléfono deben ser únicos.")
    @ApiResponse(responseCode = "201", description = "Cliente creado correctamente")
    @ApiResponse(responseCode = "400", description = "DNI, email o teléfono ya registrado")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(dto));
    }

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Buscar cliente por DNI")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<CustomerDTO> findByDni(@PathVariable String dni) {
        return ResponseEntity.ok(customerService.findByDni(dni));
    }
}