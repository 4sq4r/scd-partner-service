package kz.partnerservice.service;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.UserDTO;

public interface UserService {

    UserDTO updateOne(UserDTO userDTO) throws CustomException;
}
