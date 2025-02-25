package kz.demo.mapper;

import kz.demo.model.dto.PointOfServiceDTO;
import kz.demo.model.entity.PointOfServiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointOfServiceMapper {

    PointOfServiceDTO toDTO(PointOfServiceEntity pointOfServiceEntity);

    PointOfServiceEntity toEntity(PointOfServiceDTO pointOfServiceDTO);
}
