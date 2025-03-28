package kz.partnerservice.service.impl;

import kz.partnerservice.model.entity.MediaFileEntity;
import kz.partnerservice.model.entity.UserEntity;
import kz.partnerservice.model.entity.UserMediaFileEntity;
import kz.partnerservice.repository.UserMediaFileRepository;
import kz.partnerservice.service.UserMediaFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMediaFileServiceImpl implements UserMediaFileService {

    private final UserMediaFileRepository repository;

    @Override
    public void saveOne(UserEntity userEntity, MediaFileEntity mediaFileEntity) {
        UserMediaFileEntity entity = new UserMediaFileEntity();
        entity.setUser(userEntity);
        entity.setMediaFile(mediaFileEntity);
        repository.save(entity);
    }

    @Override
    public boolean existsImageByUserId(Long id) {
        return repository.existsByUserId(id);
    }
}
