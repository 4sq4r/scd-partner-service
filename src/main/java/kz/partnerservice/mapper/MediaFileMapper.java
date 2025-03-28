package kz.partnerservice.mapper;

import kz.partnerservice.model.dto.MediaFileDTO;
import kz.partnerservice.model.entity.MediaFileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MediaFileMapper {

    MediaFileDTO toDTO(MediaFileEntity mediaFileEntity);
}
