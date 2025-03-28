package kz.partnerservice.service.impl;

import jakarta.validation.constraints.NotNull;
import kz.partnerservice.model.entity.MediaFileEntity;
import kz.partnerservice.repository.MediaFileRepository;
import kz.partnerservice.service.MediaFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaFileServiceImpl implements MediaFileService {

    private final MediaFileRepository repository;

    @Override
    public MediaFileEntity saveOne(@NotNull String url) {
        MediaFileEntity entity = new MediaFileEntity();
        entity.setUrl(url);

        return repository.save(entity);
    }
}
