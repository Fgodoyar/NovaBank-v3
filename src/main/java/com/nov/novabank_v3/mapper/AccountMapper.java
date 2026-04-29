package com.nov.novabank_v3.mapper;

import com.nov.novabank_v3.dto.AccountDTO;
import com.nov.novabank_v3.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    AccountDTO toDTO(Account account);
    Account toEntity(AccountDTO accountDTO);
}
