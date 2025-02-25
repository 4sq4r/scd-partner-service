package kz.demo.service;

import kz.demo.exception.CustomException;
import kz.demo.model.dto.CompanyDTO;
import kz.demo.model.entity.CompanyEntity;

public interface CompanyService {

    CompanyDTO saveOne(CompanyDTO companyDTO) throws CustomException;

    CompanyDTO getOne(Long id) throws CustomException;

    CompanyDTO updateOne(Long id, CompanyDTO companyDTO) throws CustomException;

    void deleteOne(Long id) throws CustomException;

    CompanyEntity findEntityById(Long id) throws CustomException;
}
