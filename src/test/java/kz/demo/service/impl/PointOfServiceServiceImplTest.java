package kz.demo.service.impl;

import kz.demo.exception.CustomException;
import kz.demo.mapper.PointOfServiceMapper;
import kz.demo.model.dto.PointOfServiceDTO;
import kz.demo.model.entity.CompanyEntity;
import kz.demo.model.entity.PointOfServiceEntity;
import kz.demo.repository.PointOfServiceRepository;
import kz.demo.service.CompanyService;
import kz.demo.util.MessageSource;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointOfServiceServiceImplTest {

    private static final Long ID = 1L;
    private static final String PHONE_NUMBER = "+77777777777";

    @Mock
    private CompanyService companyService;
    @Mock
    private PointOfServiceRepository repository;
    @Spy
    private PointOfServiceMapper mapper = Mappers.getMapper(PointOfServiceMapper.class);
    @InjectMocks
    private PointOfServiceServiceImpl underTest;

    @Test
    void saveOne_throwsException_whenWrongPhoneFormat() {
        //given
        PointOfServiceDTO pointOfServiceDTO = Instancio.create(PointOfServiceDTO.class);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.saveOne(pointOfServiceDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.WRONG_PHONE_FORMAT.getText(pointOfServiceDTO.getPhone()), e.getMessage());
    }

    @Test
    void saveOne_throwsException_whenPhoneIsExists() {
        //given
        PointOfServiceDTO pointOfServiceDTO = Instancio.create(PointOfServiceDTO.class);
        pointOfServiceDTO.setPhone(PHONE_NUMBER);

        when(repository.existsByPhoneIgnoreCase(pointOfServiceDTO.getPhone())).thenReturn(true);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.saveOne(pointOfServiceDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.POINT_OF_SERVICE_PHONE_EXISTS.getText(pointOfServiceDTO.getPhone()), e.getMessage());
    }

    @Test
    void saveOne_throwsException_whenCompanyNotFound() throws CustomException {
        //given
        PointOfServiceDTO pointOfServiceDTO = Instancio.create(PointOfServiceDTO.class);
        pointOfServiceDTO.setPhone(PHONE_NUMBER);

        when(companyService.findEntityById(pointOfServiceDTO.getCompanyId()))
                .thenThrow(CustomException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .message(MessageSource.COMPANY_NOT_FOUND.getText(pointOfServiceDTO.getCompanyId().toString()))
                        .build());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.saveOne(pointOfServiceDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals(MessageSource.COMPANY_NOT_FOUND.getText(pointOfServiceDTO.getCompanyId().toString()), e.getMessage());
    }

    @Test
    void saveOne_savesPointOfService() throws CustomException {
        //given
        ArgumentCaptor<PointOfServiceEntity> argumentCaptor = ArgumentCaptor.forClass(PointOfServiceEntity.class);
        PointOfServiceDTO pointOfServiceDTO = Instancio.create(PointOfServiceDTO.class);
        pointOfServiceDTO.setPhone(PHONE_NUMBER);
        CompanyEntity companyEntity = Instancio.create(CompanyEntity.class);
        companyEntity.setId(pointOfServiceDTO.getCompanyId());

        when(companyService.findEntityById(pointOfServiceDTO.getCompanyId()))
                .thenReturn(companyEntity);
        //when
        PointOfServiceDTO result = underTest.saveOne(pointOfServiceDTO);
        //then
        verify(repository, times(1)).save(argumentCaptor.capture());
        PointOfServiceEntity savedEntity = argumentCaptor.getValue();

        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertEquals(pointOfServiceDTO.getCompanyId(), savedEntity.getCompany().getId());
        assertEquals(pointOfServiceDTO.getAddress(), savedEntity.getAddress());
        assertEquals(pointOfServiceDTO.getPhone(), savedEntity.getPhone());
        assertNotNull(savedEntity.getCreatedAt());
        assertNotNull(savedEntity.getUpdatedAt());

        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals(savedEntity.getCompany().getId(), result.getCompany().getId());
        assertEquals(savedEntity.getAddress(), result.getAddress());
        assertEquals(savedEntity.getPhone(), result.getPhone());
        assertEquals(savedEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(savedEntity.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void getOne_throwsException_whenPointOfServiceNotFound() {
        //given
        when(repository.findById(ID)).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.getOne(ID));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals(MessageSource.POINT_OF_SERVICE_NOT_FOUND.getText(ID.toString()), e.getMessage());
    }

    @Test
    void getOne_returnsPointOfService() throws CustomException {
        //given
        PointOfServiceEntity pointOfServiceEntity = Instancio.create(PointOfServiceEntity.class);
        pointOfServiceEntity.setId(ID);
        when(repository.findById(ID)).thenReturn(Optional.of(pointOfServiceEntity));
        //when
        PointOfServiceDTO result = underTest.getOne(ID);
        //then
        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(pointOfServiceEntity.getCompany().getId(), result.getCompany().getId());
        assertEquals(pointOfServiceEntity.getAddress(), result.getAddress());
        assertEquals(pointOfServiceEntity.getPhone(), result.getPhone());
        assertEquals(pointOfServiceEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(pointOfServiceEntity.getUpdatedAt(), result.getUpdatedAt());
    }


    @Test
    void updateOne_throwsException_whenWrongPhoneFormat() {
        //given
        PointOfServiceDTO pointOfServiceDTO = Instancio.create(PointOfServiceDTO.class);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(ID, pointOfServiceDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.WRONG_PHONE_FORMAT.getText(pointOfServiceDTO.getPhone()), e.getMessage());
    }

    @Test
    void updateOne_throwsException_whenPhoneExists() {
        //given
        PointOfServiceDTO pointOfServiceDTO = Instancio.create(PointOfServiceDTO.class);
        pointOfServiceDTO.setPhone(PHONE_NUMBER);
        PointOfServiceEntity pointOfServiceEntity = Instancio.create(PointOfServiceEntity.class);
        pointOfServiceEntity.setId(2L);
        when(repository.findByPhoneIgnoreCase(pointOfServiceDTO.getPhone())).thenReturn(Optional.of(pointOfServiceEntity));
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(ID, pointOfServiceDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.POINT_OF_SERVICE_PHONE_EXISTS.getText(pointOfServiceDTO.getPhone()), e.getMessage());
    }

    @Test
    void updateOne_updatesPointOfService() throws CustomException {
        //given
        ArgumentCaptor<PointOfServiceEntity> argumentCaptor = ArgumentCaptor.forClass(PointOfServiceEntity.class);
        PointOfServiceDTO pointOfServiceDTO = Instancio.create(PointOfServiceDTO.class);
        pointOfServiceDTO.setPhone(PHONE_NUMBER);
        PointOfServiceEntity pointOfServiceEntity = Instancio.create(PointOfServiceEntity.class);
        pointOfServiceEntity.setId(ID);
        when(repository.findByPhoneIgnoreCase(pointOfServiceDTO.getPhone())).thenReturn(Optional.of(pointOfServiceEntity));
        when(repository.findById(ID)).thenReturn(Optional.of(pointOfServiceEntity));
        //when
        PointOfServiceDTO result = underTest.updateOne(ID, pointOfServiceDTO);
        //then
        verify(repository, times(1)).save(argumentCaptor.capture());
        PointOfServiceEntity savedEntity = argumentCaptor.getValue();

        assertNotNull(savedEntity);
        assertEquals(ID, savedEntity.getId());
        assertEquals(pointOfServiceDTO.getPhone(), savedEntity.getPhone());
        assertEquals(pointOfServiceDTO.getAddress(), savedEntity.getAddress());
        assertEquals(pointOfServiceEntity.getCompany(), savedEntity.getCompany());
        assertNotNull(savedEntity.getCreatedAt());
        assertNotNull(savedEntity.getUpdatedAt());

        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals(savedEntity.getPhone(), result.getPhone());
        assertEquals(savedEntity.getAddress(), result.getAddress());
        assertEquals(savedEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(savedEntity.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void deleteOne_throwsException_whenPointOfServiceNotFound() {
        //given
        when(repository.findById(ID)).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.deleteOne(ID));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals(MessageSource.POINT_OF_SERVICE_NOT_FOUND.getText(ID.toString()), e.getMessage());
    }

    @Test
    void deleteOne_deletesPointOfService() throws CustomException {
        //given
        PointOfServiceEntity pointOfServiceEntity = Instancio.create(PointOfServiceEntity.class);
        pointOfServiceEntity.setId(ID);

        when(repository.findById(ID)).thenReturn(Optional.of(pointOfServiceEntity));
        //when
        underTest.deleteOne(ID);
        //then
        verify(repository, times(1)).delete(pointOfServiceEntity);
    }

    @Test
    void findEntityById_throwsException_whenEntityNotFound() {
        //given
        when(repository.findById(ID)).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.findEntityById(ID));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals(MessageSource.POINT_OF_SERVICE_NOT_FOUND.getText(ID.toString()), e.getMessage());
    }

    @Test
    void findEntityById_returnsEntity() throws CustomException {
        //given
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        PointOfServiceEntity pointOfServiceEntity = Instancio.create(PointOfServiceEntity.class);
        pointOfServiceEntity.setId(ID);
        when(repository.findById(ID)).thenReturn(Optional.of(pointOfServiceEntity));
        //when
        PointOfServiceEntity result = underTest.findEntityById(ID);
        //then
        verify(repository, times(1)).findById(argumentCaptor.capture());
        Long id = argumentCaptor.getValue();
        assertEquals(ID, id);

        assertNotNull(result);
        assertEquals(ID, result.getId());
    }
}