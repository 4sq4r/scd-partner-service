package kz.demo.service.impl;

import kz.demo.exception.CustomException;
import kz.demo.mapper.CompanyMapper;
import kz.demo.model.dto.CompanyDTO;
import kz.demo.model.entity.CompanyEntity;
import kz.demo.repository.CompanyRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    private static final Long ID = 1L;
    private static final String PHONE_NUMBER = "+77777777777";
    @Mock
    private CompanyRepository repository;
    @Spy
    private CompanyMapper mapper = Mappers.getMapper(CompanyMapper.class);
    @InjectMocks
    private CompanyServiceImpl underTest;


    @Test
    void saveOne_throwsException_whenWrongPhoneFormat() {
        //given
        CompanyDTO companyDTO = Instancio.create(CompanyDTO.class);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.saveOne(companyDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.WRONG_PHONE_FORMAT.getText(companyDTO.getPhone()), e.getMessage());
    }

    @Test
    void saveOne_throwsException_whenCompanyPhoneExists() {
        //given
        CompanyDTO companyDTO = Instancio.create(CompanyDTO.class);
        companyDTO.setPhone(PHONE_NUMBER);

        when(repository.existsByPhoneIgnoreCase(companyDTO.getPhone())).thenReturn(true);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.saveOne(companyDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.COMPANY_PHONE_EXISTS.getText(companyDTO.getPhone()), e.getMessage());
    }

    @Test
    void saveOne_throwsException_whenCompanyNameExists() {
        //given
        CompanyDTO companyDTO = Instancio.create(CompanyDTO.class);
        companyDTO.setPhone(PHONE_NUMBER);

        when(repository.existsByPhoneIgnoreCase(companyDTO.getPhone())).thenReturn(false);
        when(repository.existsByNameIgnoreCase(companyDTO.getName())).thenReturn(true);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.saveOne(companyDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.COMPANY_NAME_EXISTS.getText(companyDTO.getName()), e.getMessage());
    }

    @Test
    void saveOne_savesCompany() throws CustomException {
        //given
        ArgumentCaptor<CompanyEntity> argumentCaptor = ArgumentCaptor.forClass(CompanyEntity.class);
        CompanyDTO companyDTO = Instancio.create(CompanyDTO.class);
        companyDTO.setPhone(PHONE_NUMBER);
        CompanyEntity companyEntity = mapper.toEntity(companyDTO);

        when(repository.save(any())).thenReturn(companyEntity);
        //when
        CompanyDTO result = underTest.saveOne(companyDTO);
        //then
        verify(repository, times(1)).save(argumentCaptor.capture());
        CompanyEntity savedEntity = argumentCaptor.getValue();

        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertEquals(companyDTO.getPhone(), savedEntity.getPhone());
        assertEquals(companyDTO.getPassword(), savedEntity.getPassword());
        assertEquals(companyDTO.getName(), savedEntity.getName());
        assertNotNull(savedEntity.getCreatedAt());
        assertNotNull(savedEntity.getUpdatedAt());

        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals(savedEntity.getPhone(), result.getPhone());
        assertEquals(savedEntity.getPassword(), result.getPassword());
        assertEquals(savedEntity.getName(), result.getName());
        assertEquals(savedEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(savedEntity.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void getOne_throwsException_whenCompanyNotFound() {
        //given
        when(repository.findById(ID)).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.getOne(ID));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals(MessageSource.COMPANY_NOT_FOUND.getText(ID.toString()), e.getMessage());
    }

    @Test
    void getOne_returnsCompany() throws CustomException {
        //given
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        CompanyEntity companyEntity = Instancio.create(CompanyEntity.class);
        companyEntity.setId(ID);

        when(repository.findById(ID)).thenReturn(Optional.of(companyEntity));
        //when
        CompanyDTO result = underTest.getOne(ID);
        //then
        verify(repository, times(1)).findById(argumentCaptor.capture());
        Long id = argumentCaptor.getValue();
        assertEquals(ID, id);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertNotNull(result.getName());
        assertNotNull(result.getPassword());
        assertNotNull(result.getPhone());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void deleteOne_throwsException_whenCompanyNotFound() {
        //given
        when(repository.findById(ID)).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.deleteOne(ID));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals(MessageSource.COMPANY_NOT_FOUND.getText(ID.toString()), e.getMessage());
    }

    @Test
    void deleteOne_deletesCompany() throws CustomException {
        //given
        CompanyEntity companyEntity = Instancio.create(CompanyEntity.class);
        companyEntity.setId(ID);

        when(repository.findById(ID)).thenReturn(Optional.of(companyEntity));
        //when
        underTest.deleteOne(ID);
        //then
        verify(repository, times(1)).delete(companyEntity);
    }

    @Test
    void updateOne_throwsException_whenWrongPhoneFormat() {
        //given
        CompanyDTO companyDTO = Instancio.create(CompanyDTO.class);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(ID, companyDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.WRONG_PHONE_FORMAT.getText(companyDTO.getPhone()), e.getMessage());
    }

    @Test
    void updateOne_throwsException_whenCompanyPhoneExists() {
        //given
        CompanyDTO companyDTO = Instancio.create(CompanyDTO.class);
        companyDTO.setPhone(PHONE_NUMBER);
        CompanyEntity companyEntity = Instancio.create(CompanyEntity.class);
        companyEntity.setId(ID);
        CompanyEntity companyByPhone = Instancio.create(CompanyEntity.class);
        companyByPhone.setId(2L);

        when(repository.findByPhoneIgnoreCase(companyDTO.getPhone())).thenReturn(Optional.of(companyByPhone));
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(ID, companyDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.COMPANY_PHONE_EXISTS.getText(companyDTO.getPhone()), e.getMessage());
    }

    @Test
    void updateOne_throwsException_whenCompanyNameExists() {
        //given
        CompanyDTO companyDTO = Instancio.create(CompanyDTO.class);
        companyDTO.setPhone(PHONE_NUMBER);
        CompanyEntity companyEntity = Instancio.create(CompanyEntity.class);
        companyEntity.setId(ID);
        CompanyEntity companyByName = Instancio.create(CompanyEntity.class);
        companyByName.setId(2L);

        when(repository.findByNameIgnoreCase(companyDTO.getName())).thenReturn(Optional.of(companyByName));
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(ID, companyDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.COMPANY_NAME_EXISTS.getText(companyDTO.getName()), e.getMessage());
    }


    @Test
    void updateOne_throwsException_whenCompanyNotFound() {
        //given
        CompanyDTO companyDTO = Instancio.create(CompanyDTO.class);
        companyDTO.setPhone(PHONE_NUMBER);

        when(repository.findById(ID)).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(ID, companyDTO));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals(MessageSource.COMPANY_NOT_FOUND.getText(ID.toString()), e.getMessage());
    }

    @Test
    void updateOne_updatesCompany() throws CustomException {
        //given
        ArgumentCaptor<CompanyEntity> argumentCaptor = ArgumentCaptor.forClass(CompanyEntity.class);
        CompanyDTO companyDTO = Instancio.create(CompanyDTO.class);
        companyDTO.setId(ID);
        companyDTO.setPhone(PHONE_NUMBER);
        CompanyEntity companyEntity = Instancio.create(CompanyEntity.class);
        companyEntity.setId(ID);

        when(repository.findById(ID)).thenReturn(Optional.of(companyEntity));
        //when
        CompanyDTO result = underTest.updateOne(ID, companyDTO);
        //then
        verify(repository, times(1)).save(argumentCaptor.capture());
        CompanyEntity savedEntity = argumentCaptor.getValue();

        assertNotNull(savedEntity);
        assertEquals(ID, savedEntity.getId());
        assertEquals(companyDTO.getPhone(), savedEntity.getPhone());
        assertEquals(companyDTO.getPassword(), savedEntity.getPassword());
        assertEquals(companyDTO.getName(), savedEntity.getName());
        assertNotNull(savedEntity.getCreatedAt());
        assertNotNull(savedEntity.getUpdatedAt());

        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals(savedEntity.getPhone(), result.getPhone());
        assertEquals(savedEntity.getPassword(), result.getPassword());
        assertEquals(savedEntity.getName(), result.getName());
        assertEquals(savedEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(savedEntity.getUpdatedAt(), result.getUpdatedAt());
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
        assertEquals(MessageSource.COMPANY_NOT_FOUND.getText(ID.toString()), e.getMessage());
    }

    @Test
    void findEntityById_returnsEntity() throws CustomException {
        //given
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        CompanyEntity companyEntity = Instancio.create(CompanyEntity.class);
        companyEntity.setId(ID);
        when(repository.findById(ID)).thenReturn(Optional.of(companyEntity));
        //when
        CompanyEntity result = underTest.findEntityById(ID);
        //then
        verify(repository, times(1)).findById(argumentCaptor.capture());
        Long id = argumentCaptor.getValue();
        assertEquals(ID, id);

        assertNotNull(result);
        assertEquals(ID, result.getId());
    }
}