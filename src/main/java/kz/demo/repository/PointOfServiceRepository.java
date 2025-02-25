package kz.demo.repository;

import kz.demo.model.entity.PointOfServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointOfServiceRepository extends JpaRepository<PointOfServiceEntity, Long> {

    boolean existsByPhoneIgnoreCase(String phone);

    Optional<PointOfServiceEntity> findByPhoneIgnoreCase(String phone);
}
