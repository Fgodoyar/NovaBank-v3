package com.nov.novabank_v3.mapper;

import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {

    TransactionDTO toDTO(Transaction transaction);
    Transaction toEntity(TransactionDTO transactionDTO);
}
