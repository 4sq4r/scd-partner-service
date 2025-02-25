package kz.demo.service;

import kz.demo.exception.CustomException;
import kz.demo.model.dto.PointOfServiceDTO;
import kz.demo.model.entity.PointOfServiceEntity;

public interface PointOfServiceService {

    PointOfServiceDTO saveOne(PointOfServiceDTO pointOfServiceDTO) throws CustomException;

    PointOfServiceDTO getOne(Long id) throws CustomException;

    PointOfServiceDTO updateOne(Long id, PointOfServiceDTO pointOfServiceDTO) throws CustomException;

    void deleteOne(Long id) throws CustomException;

    PointOfServiceEntity findEntityById(Long id) throws CustomException;
}
