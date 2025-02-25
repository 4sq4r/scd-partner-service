package kz.demo.service.impl;

import kz.demo.exception.CustomException;
import kz.demo.mapper.PointOfServiceMapper;
import kz.demo.model.dto.PointOfServiceDTO;
import kz.demo.model.entity.CompanyEntity;
import kz.demo.model.entity.PointOfServiceEntity;
import kz.demo.repository.PointOfServiceRepository;
import kz.demo.service.CompanyService;
import kz.demo.service.PointOfServiceService;
import kz.demo.util.MessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static kz.demo.util.ScdStringUtils.PHONE_REGEX;

@Service
@RequiredArgsConstructor
public class PointOfServiceServiceImpl implements PointOfServiceService {

    private final PointOfServiceRepository repository;
    private final CompanyService companyService;
    private final PointOfServiceMapper mapper;

    @Override
    public PointOfServiceDTO saveOne(PointOfServiceDTO pointOfServiceDTO) throws CustomException {
        formatPointOfServiceDTO(pointOfServiceDTO);
        validatePointOfServiceDTOBeforeSave(pointOfServiceDTO);
        CompanyEntity companyEntity = companyService.findEntityById(pointOfServiceDTO.getCompanyId());
        PointOfServiceEntity pointOfServiceEntity = mapper.toEntity(pointOfServiceDTO);
        pointOfServiceEntity.setCompany(companyEntity);
        repository.save(pointOfServiceEntity);

        return mapper.toDTO(pointOfServiceEntity);
    }

    @Override
    public PointOfServiceDTO getOne(Long id) throws CustomException {
        PointOfServiceEntity pointOfServiceEntity = findEntityById(id);

        return mapper.toDTO(pointOfServiceEntity);
    }

    @Override
    public PointOfServiceDTO updateOne(Long id, PointOfServiceDTO pointOfServiceDTO) throws CustomException {
        formatPointOfServiceDTO(pointOfServiceDTO);
        validatePointOfServiceDTOBeforeUpdate(id, pointOfServiceDTO);
        PointOfServiceEntity pointOfServiceEntity = findEntityById(id);
        pointOfServiceEntity.setAddress(pointOfServiceDTO.getAddress());
        pointOfServiceEntity.setPhone(pointOfServiceDTO.getPhone());
        repository.save(pointOfServiceEntity);

        return mapper.toDTO(pointOfServiceEntity);
    }

    @Override
    public void deleteOne(Long id) throws CustomException {
        PointOfServiceEntity pointOfServiceEntity = findEntityById(id);
        repository.delete(pointOfServiceEntity);
    }

    @Override
    public PointOfServiceEntity findEntityById(Long id) throws CustomException {
        return repository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .message(MessageSource.POINT_OF_SERVICE_NOT_FOUND.getText(id.toString()))
                        .build()
        );
    }

    private void formatPointOfServiceDTO(PointOfServiceDTO pointOfServiceDTO) {
        pointOfServiceDTO.setAddress(pointOfServiceDTO.getAddress().trim());
        pointOfServiceDTO.setPhone(pointOfServiceDTO.getPhone().trim());
    }

    private void validatePointOfServiceDTOBeforeSave(PointOfServiceDTO pointOfServiceDTO) throws CustomException {
        String phone = pointOfServiceDTO.getPhone();

        if (!phone.matches(PHONE_REGEX)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.WRONG_PHONE_FORMAT.getText(phone))
                    .build();
        } else if (repository.existsByPhoneIgnoreCase(phone)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.POINT_OF_SERVICE_PHONE_EXISTS.getText(phone))
                    .build();
        }
    }

    private void validatePointOfServiceDTOBeforeUpdate(Long id, PointOfServiceDTO pointOfServiceDTO) throws CustomException {
        String phone = pointOfServiceDTO.getPhone();

        if (!phone.matches(PHONE_REGEX)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.WRONG_PHONE_FORMAT.getText(phone))
                    .build();
        }

        Optional<PointOfServiceEntity> optionalByPhone = repository.findByPhoneIgnoreCase(phone);

        if (optionalByPhone.isPresent() && !Objects.equals(optionalByPhone.get().getId(), id)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.POINT_OF_SERVICE_PHONE_EXISTS.getText(phone))
                    .build();
        }
    }
}
