package kz.partnerservice.service.impl;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.mapper.JobMapper;
import kz.partnerservice.model.dto.JobDTO;
import kz.partnerservice.model.entity.JobEntity;
import kz.partnerservice.model.entity.UserEntity;
import kz.partnerservice.repository.JobRepository;
import kz.partnerservice.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kz.partnerservice.util.MessageSource.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobMapper jobMapper;
    private final JobRepository jobRepository;
    private final UserServiceImpl userService;

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public JobDTO saveOne(JobDTO jobDTO) throws CustomException {
        UserEntity userEntity = userService.getUserEntityFromContext();
        formatJobDTO(jobDTO);

        if (jobRepository.existsByNameIgnoreCaseAndUserId(jobDTO.getName(), userEntity.getId())) {
            throw CustomException.builder()
                    .httpStatus(BAD_REQUEST)
                    .message(JOB_ALREADY_EXISTS.getText())
                    .build();
        }

        JobEntity jobEntity = jobMapper.toEntity(jobDTO);
        jobEntity.setUser(userEntity);
        jobRepository.save(jobEntity);

        return jobMapper.toDTO(jobEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public JobDTO getOne(Long id) throws CustomException {
        JobEntity jobEntity = findEntityById(id);
        checkAccess(jobEntity);

        return jobMapper.toDTO(jobEntity);
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public JobDTO updateOne(Long id, JobDTO jobDTO) throws CustomException {
        UserEntity userEntity = userService.getUserEntityFromContext();
        formatJobDTO(jobDTO);

        if (jobRepository.existCheckBeforeUpdate(jobDTO.getName(), userEntity.getId(), id)) {
            throw CustomException.builder()
                    .httpStatus(BAD_REQUEST)
                    .message(JOB_ALREADY_EXISTS.getText())
                    .build();
        }

        JobEntity jobEntity = findEntityById(id);
        jobEntity.setName(jobDTO.getName());
        jobEntity.setDescription(jobDTO.getDescription());
        jobEntity.setPrice(jobDTO.getPrice());
        jobEntity.setDuration(jobDTO.getDuration());
        jobRepository.save(jobEntity);

        return jobMapper.toDTO(jobEntity);
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public void deleteOne(Long id) throws CustomException {
        JobEntity jobEntity = findEntityById(id);
        checkAccess(jobEntity);
        jobRepository.delete(jobEntity);
    }

    private void formatJobDTO(JobDTO jobDTO) {
        jobDTO.setName(jobDTO.getName().trim());
        jobDTO.setDescription(jobDTO.getDescription().trim());
    }

    private JobEntity findEntityById(Long id) throws CustomException {
        return jobRepository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(BAD_REQUEST)
                        .message(JOB_NOT_FOUND.getText(id.toString()))
                        .build()
        );
    }

    private void checkAccess(JobEntity jobEntity) throws CustomException {
        UserEntity userEntity = userService.getUserEntityFromContext();

        if (!jobEntity.getUser().getId().equals(userEntity.getId())) {
            throw CustomException.builder()
                    .httpStatus(FORBIDDEN)
                    .message(ACCESS_DENIED.getText())
                    .build();
        }
    }
}
