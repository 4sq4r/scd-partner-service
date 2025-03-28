package kz.partnerservice.service;

import kz.partnerservice.model.entity.MediaFileEntity;

public interface MediaFileService {

    MediaFileEntity saveOne(String url);
}
