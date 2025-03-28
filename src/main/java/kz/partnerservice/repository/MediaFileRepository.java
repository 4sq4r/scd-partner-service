package kz.partnerservice.repository;

import kz.partnerservice.model.entity.MediaFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFileEntity, Long> {
}
