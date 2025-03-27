package kz.partnerservice.mapper;

import kz.partnerservice.model.dto.JobDTO;
import kz.partnerservice.model.entity.JobEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobMapper {

    JobDTO toDTO(JobEntity jobEntity);

    JobEntity toEntity(JobDTO jobDTO);
}
