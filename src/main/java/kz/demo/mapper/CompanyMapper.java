package kz.demo.mapper;

import kz.demo.model.dto.CompanyDTO;
import kz.demo.model.entity.CompanyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyDTO toDTO(CompanyEntity companyEntity);

    CompanyEntity toEntity(CompanyDTO companyDTO);
}
