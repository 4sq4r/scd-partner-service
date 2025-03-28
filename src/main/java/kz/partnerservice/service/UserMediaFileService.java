package kz.partnerservice.service;

import kz.partnerservice.model.entity.MediaFileEntity;
import kz.partnerservice.model.entity.UserEntity;

public interface UserMediaFileService {

    void saveOne(UserEntity userEntity, MediaFileEntity mediaFileEntity);

    boolean existsImageByUserId(Long id);
}
