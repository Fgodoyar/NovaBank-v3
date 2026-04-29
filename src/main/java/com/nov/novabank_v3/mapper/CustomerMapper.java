package com.nov.novabank_v3.mapper;

import com.nov.novabank_v3.dto.CustomerDTO;
import com.nov.novabank_v3.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    CustomerDTO toDTO(Customer customer);
    Customer toEntity(CustomerDTO customerDTO);
}