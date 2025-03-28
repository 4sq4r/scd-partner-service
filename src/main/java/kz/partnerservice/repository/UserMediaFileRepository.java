package kz.partnerservice.repository;

import kz.partnerservice.model.entity.UserMediaFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMediaFileRepository extends JpaRepository<UserMediaFileEntity, Long> {

    boolean existsByUserId(Long id);
}
