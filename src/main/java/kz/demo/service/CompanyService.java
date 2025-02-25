package kz.demo.service;

import kz.demo.exception.CustomException;
import kz.demo.model.dto.CompanyDTO;

public interface CompanyService {

    CompanyDTO saveOne(CompanyDTO companyDTO) throws CustomException;

    CompanyDTO getOne(Long id) throws CustomException;

    CompanyDTO updateOne(Long id, CompanyDTO companyDTO) throws CustomException;

    void deleteOne(Long id) throws CustomException;
}
