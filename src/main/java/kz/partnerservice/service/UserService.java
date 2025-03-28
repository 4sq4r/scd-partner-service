package kz.partnerservice.service;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.MediaFileDTO;
import kz.partnerservice.model.dto.UserDTO;
import kz.partnerservice.model.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserDTO updateOne(UserDTO userDTO) throws CustomException;

    UserEntity getUserEntityFromContext() throws CustomException;

    MediaFileDTO uploadImage(MultipartFile file) throws CustomException;

    UserDTO getInfo() throws CustomException;
}
