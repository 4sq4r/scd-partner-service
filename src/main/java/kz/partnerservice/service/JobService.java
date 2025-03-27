package kz.partnerservice.service;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.JobDTO;

public interface JobService {

    JobDTO saveOne(JobDTO jobDTO) throws CustomException;

    JobDTO getOne(Long id) throws CustomException;

    JobDTO updateOne(Long id, JobDTO jobDTO) throws CustomException;

    void deleteOne(Long id) throws CustomException;
}
