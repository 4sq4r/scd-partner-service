package kz.demo.repository;

import kz.demo.model.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    boolean existsByPhoneIgnoreCase(String phone);

    boolean existsByNameIgnoreCase(String name);

    Optional<CompanyEntity> findByPhoneIgnoreCase(String phone);

    Optional<CompanyEntity> findByNameIgnoreCase(String name);
}
