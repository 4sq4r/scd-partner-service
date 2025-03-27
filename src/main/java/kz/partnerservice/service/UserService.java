package kz.partnerservice.service;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.UserDTO;
import kz.partnerservice.model.entity.UserEntity;

public interface UserService {

    UserDTO updateOne(UserDTO userDTO) throws CustomException;

    UserEntity getUserEntityFromContext() throws CustomException;
}
