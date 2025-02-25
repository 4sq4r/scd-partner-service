package kz.demo.service.impl;

import kz.demo.exception.CustomException;
import kz.demo.mapper.CompanyMapper;
import kz.demo.model.dto.CompanyDTO;
import kz.demo.model.entity.CompanyEntity;
import kz.demo.repository.CompanyRepository;
import kz.demo.service.CompanyService;
import kz.demo.util.MessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {
    private static final String PHONE_REGEX = "^(\\+7|7)\\d{10}$";

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;


    @Override
    @Transactional(rollbackFor = CustomException.class)
    public CompanyDTO saveOne(CompanyDTO companyDTO) throws CustomException {
        formatCompanyDTO(companyDTO);
        validateBeforeSave(companyDTO);
        CompanyEntity companyEntity = companyMapper.toEntity(companyDTO);
        companyRepository.save(companyEntity);

        return companyMapper.toDTO(companyEntity);
    }

    @Override
    public CompanyDTO getOne(Long id) throws CustomException {
        CompanyEntity companyEntity = findEntityById(id);

        return companyMapper.toDTO(companyEntity);
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public CompanyDTO updateOne(Long id, CompanyDTO companyDTO) throws CustomException {
        formatCompanyDTO(companyDTO);
        validateBeforeUpdate(id, companyDTO);
        CompanyEntity companyEntity = findEntityById(id);
        companyEntity.setName(companyDTO.getName());
        companyEntity.setPhone(companyDTO.getPhone());
        companyEntity.setPassword(companyDTO.getPassword());
        companyRepository.save(companyEntity);

        return companyMapper.toDTO(companyEntity);
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public void deleteOne(Long id) throws CustomException {
        CompanyEntity companyEntity = findEntityById(id);
        companyRepository.delete(companyEntity);
    }

    private CompanyEntity findEntityById(Long id) throws CustomException {
        return companyRepository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .message(MessageSource.COMPANY_NOT_FOUND.getText(id.toString()))
                        .build());
    }

    private void formatCompanyDTO(CompanyDTO companyDTO) {
        companyDTO.setName(companyDTO.getName().trim());
        companyDTO.setPhone(companyDTO.getPhone().trim());
        companyDTO.setPassword(companyDTO.getPassword().trim());
    }

    private void validateBeforeSave(CompanyDTO companyDTO) throws CustomException {
        String phone = companyDTO.getPhone();
        String name = companyDTO.getName();

        if (!phone.matches(PHONE_REGEX)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.WRONG_PHONE_FORMAT.getText(phone))
                    .build();
        } else if (companyRepository.existsByPhoneIgnoreCase(phone)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.COMPANY_PHONE_EXISTS.getText(phone))
                    .build();
        } else if (companyRepository.existsByNameIgnoreCase(name)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.COMPANY_NAME_EXISTS.getText(name))
                    .build();
        }
    }

    private void validateBeforeUpdate(Long id, CompanyDTO companyDTO) throws CustomException {
        String phone = companyDTO.getPhone();
        Optional<CompanyEntity> optionalCompanyByPhone = companyRepository.findByPhoneIgnoreCase(companyDTO.getPhone());
        Optional<CompanyEntity> optionalCompanyByName = companyRepository.findByNameIgnoreCase(companyDTO.getName());

        if (!phone.matches(PHONE_REGEX)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.WRONG_PHONE_FORMAT.getText(phone))
                    .build();
        } else if (optionalCompanyByPhone.isPresent() && !Objects.equals(optionalCompanyByPhone.get().getId(), id)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.COMPANY_PHONE_EXISTS.getText(companyDTO.getPhone()))
                    .build();
        } else if (optionalCompanyByName.isPresent() && !Objects.equals(optionalCompanyByName.get().getId(), id)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(MessageSource.COMPANY_NAME_EXISTS.getText(companyDTO.getName()))
                    .build();
        }
    }
}
