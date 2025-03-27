package kz.partnerservice.repository;

import kz.partnerservice.model.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {

    boolean existsByNameIgnoreCaseAndUserId(String jobName,
                                            Long userId);

    @Query(nativeQuery = true,
            value = """
                    select exists(
                        select 1
                        from jobs
                        where user_id = :userId
                            and name = :jobName
                            and id != :jobId);
                    """)
    boolean existCheckBeforeUpdate(@Param("jobName") String jobName,
                                   @Param("userId") Long userId,
                                   @Param("jobId") Long jobId);
}
