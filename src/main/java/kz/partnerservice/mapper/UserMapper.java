package kz.partnerservice.mapper;

import kz.partnerservice.model.dto.UserDTO;
import kz.partnerservice.model.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(UserEntity userEntity);
}
