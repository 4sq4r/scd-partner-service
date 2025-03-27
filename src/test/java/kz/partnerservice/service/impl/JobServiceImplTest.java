package kz.partnerservice.service.impl;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.mapper.JobMapper;
import kz.partnerservice.model.dto.JobDTO;
import kz.partnerservice.model.entity.JobEntity;
import kz.partnerservice.model.entity.UserEntity;
import kz.partnerservice.repository.JobRepository;
import kz.partnerservice.util.MessageSource;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kz.partnerservice.util.MessageSource.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserServiceImpl userService;

    @Spy
    private JobMapper jobMapper = Mappers.getMapper(JobMapper.class);

    @InjectMocks
    private JobServiceImpl underTest;

    @Test
    void saveOne_throwsException_whenJobAlreadyExists() throws CustomException {
        //given
        UserEntity userEntity = Instancio.create(UserEntity.class);
        JobDTO jobDTO = Instancio.create(JobDTO.class);
        when(userService.getUserEntityFromContext()).thenReturn(userEntity);
        when(jobRepository.existsByNameIgnoreCaseAndUserId(jobDTO.getName(), userEntity.getId()))
                .thenReturn(true);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.saveOne(jobDTO));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.JOB_ALREADY_EXISTS.getText(), e.getMessage());
    }

    @Test
    void saveOne_savesJob() throws CustomException {
        //given
        ArgumentCaptor<JobEntity> jobEntityArgumentCaptor = ArgumentCaptor.forClass(JobEntity.class);
        UserEntity userEntity = Instancio.create(UserEntity.class);
        JobDTO jobDTO = Instancio.create(JobDTO.class);
        when(userService.getUserEntityFromContext()).thenReturn(userEntity);
        when(jobRepository.existsByNameIgnoreCaseAndUserId(jobDTO.getName(), userEntity.getId()))
                .thenReturn(false);
        //when
        JobDTO result = underTest.saveOne(jobDTO);
        //then
        verify(jobRepository).save(jobEntityArgumentCaptor.capture());
        JobEntity savedJobEntity = jobEntityArgumentCaptor.getValue();
        assertEquals(jobDTO.getName(), savedJobEntity.getName());
        assertEquals(jobDTO.getDescription(), savedJobEntity.getDescription());
        assertEquals(jobDTO.getPrice(), savedJobEntity.getPrice());
        assertEquals(jobDTO.getDuration(), savedJobEntity.getDuration());
        assertEquals(userEntity.getId(), savedJobEntity.getUser().getId());

        assertEquals(savedJobEntity.getId(), result.getId());
        assertEquals(savedJobEntity.getName(), result.getName());
        assertEquals(savedJobEntity.getDescription(), result.getDescription());
        assertEquals(savedJobEntity.getPrice(), result.getPrice());
        assertEquals(savedJobEntity.getDuration(), result.getDuration());
    }

    @Test
    void getOne_throwsException_whenJobNotFound() {
        //given
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.getOne(1L));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(JOB_NOT_FOUND.getText(String.valueOf(1L)), e.getMessage());
    }

    @Test
    void getOne_throwsException_whenAccessDenied() throws CustomException {
        //given
        JobEntity jobEntity = Instancio.create(JobEntity.class);
        jobEntity.setId(1L);
        UserEntity userEntity = Instancio.create(UserEntity.class);
        userEntity.setId(2L);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(jobEntity));
        when(userService.getUserEntityFromContext()).thenReturn(userEntity);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.getOne(1L));
        //then
        assertNotNull(e);
        assertEquals(FORBIDDEN, e.getHttpStatus());
        assertEquals(ACCESS_DENIED.getText(), e.getMessage());
    }

    @Test
    void getOne_returnsJob() throws CustomException {
        //given
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        JobEntity jobEntity = Instancio.create(JobEntity.class);
        jobEntity.getUser().setId(1L);
        UserEntity userEntity = Instancio.create(UserEntity.class);
        userEntity.setId(1L);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(jobEntity));
        when(userService.getUserEntityFromContext()).thenReturn(userEntity);
        //when
        JobDTO result = underTest.getOne(1L);
        //then
        verify(jobRepository).findById(idArgumentCaptor.capture());
        Long id = idArgumentCaptor.getValue();
        assertEquals(1L, id);
        assertEquals(jobEntity.getId(), result.getId());
        assertEquals(jobEntity.getName(), result.getName());
        assertEquals(jobEntity.getDescription(), result.getDescription());
        assertEquals(jobEntity.getPrice(), result.getPrice());
        assertEquals(jobEntity.getDuration(), result.getDuration());

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertEquals(jobEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(jobEntity.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void updateOne_throwsException_whenExistCheckBeforeUpdate() throws CustomException {
        //given
        UserEntity userEntity = Instancio.create(UserEntity.class);
        when(userService.getUserEntityFromContext()).thenReturn(userEntity);
        when(jobRepository.existCheckBeforeUpdate(anyString(), anyLong(), anyLong())).thenReturn(true);
        //when
        CustomException e = assertThrows(CustomException.class,
                () -> underTest.updateOne(1L, Instancio.create(JobDTO.class)));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(JOB_ALREADY_EXISTS.getText(), e.getMessage());
    }

    @Test
    void updateOne_updatesJob() throws CustomException {
        //given
        ArgumentCaptor<JobEntity> jobEntityArgumentCaptor = ArgumentCaptor.forClass(JobEntity.class);
        JobDTO jobDTO = Instancio.create(JobDTO.class);
        UserEntity userEntity = Instancio.create(UserEntity.class);
        when(userService.getUserEntityFromContext()).thenReturn(userEntity);
        when(jobRepository.existCheckBeforeUpdate(anyString(), anyLong(), anyLong())).thenReturn(false);
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(Instancio.create(JobEntity.class)));
        //when
        JobDTO result = underTest.updateOne(1L, jobDTO);
        //then
        verify(jobRepository).save(jobEntityArgumentCaptor.capture());
        JobEntity savedJobEntity = jobEntityArgumentCaptor.getValue();
        assertEquals(jobDTO.getName(), savedJobEntity.getName());
        assertEquals(jobDTO.getDescription(), savedJobEntity.getDescription());
        assertEquals(jobDTO.getPrice(), savedJobEntity.getPrice());
        assertEquals(jobDTO.getDuration(), savedJobEntity.getDuration());

        assertEquals(savedJobEntity.getId(), result.getId());
        assertEquals(savedJobEntity.getName(), result.getName());
        assertEquals(savedJobEntity.getDescription(), result.getDescription());
        assertEquals(savedJobEntity.getPrice(), result.getPrice());
        assertEquals(savedJobEntity.getDuration(), result.getDuration());
        assertEquals(savedJobEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(savedJobEntity.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void deleteOne_throwsException_whenJobNotFound() {
        //given
        when(jobRepository.findById(anyLong())).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.deleteOne(1L));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(JOB_NOT_FOUND.getText(String.valueOf(1L)), e.getMessage());
    }

    @Test
    void deleteOne_throwsException_whenAccessDenied() throws CustomException {
        //given
        JobEntity jobEntity = Instancio.create(JobEntity.class);
        jobEntity.getUser().setId(1L);
        UserEntity userEntity = Instancio.create(UserEntity.class);
        userEntity.setId(2L);
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(jobEntity));
        when(userService.getUserEntityFromContext()).thenReturn(userEntity);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.deleteOne(1L));
        //then
        assertNotNull(e);
        assertEquals(FORBIDDEN, e.getHttpStatus());
        assertEquals(ACCESS_DENIED.getText(), e.getMessage());
    }

    @Test
    void deleteOne_deletesJob() throws CustomException {
        //given
        ArgumentCaptor<JobEntity> jobEntityArgumentCaptor = ArgumentCaptor.forClass(JobEntity.class);
        JobEntity jobEntity = Instancio.create(JobEntity.class);
        jobEntity.getUser().setId(1L);
        UserEntity userEntity = Instancio.create(UserEntity.class);
        userEntity.setId(1L);
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(jobEntity));
        when(userService.getUserEntityFromContext()).thenReturn(userEntity);
        //when
        underTest.deleteOne(jobEntity.getId());
        //then
        verify(jobRepository).delete(jobEntityArgumentCaptor.capture());
        JobEntity deletedJobEntity = jobEntityArgumentCaptor.getValue();
        assertEquals(jobEntity.getId(), deletedJobEntity.getId());
    }
}