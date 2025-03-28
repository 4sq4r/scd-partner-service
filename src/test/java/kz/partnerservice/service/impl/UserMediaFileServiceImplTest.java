package kz.partnerservice.service.impl;

import kz.partnerservice.model.entity.MediaFileEntity;
import kz.partnerservice.model.entity.UserEntity;
import kz.partnerservice.model.entity.UserMediaFileEntity;
import kz.partnerservice.repository.UserMediaFileRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserMediaFileServiceImplTest {

    @Mock
    private UserMediaFileRepository userMediaFileRepository;

    @InjectMocks
    private UserMediaFileServiceImpl underTest;

    @Test
    void saveOne_savesUserMediaFile() {
        //given
        ArgumentCaptor<UserMediaFileEntity> userMediaFileEntityArgumentCaptor =
                ArgumentCaptor.forClass(UserMediaFileEntity.class);
        UserEntity userEntity = Instancio.create(UserEntity.class);
        MediaFileEntity mediaFileEntity = Instancio.create(MediaFileEntity.class);
        //when
        underTest.saveOne(userEntity, mediaFileEntity);
        //then
        verify(userMediaFileRepository).save(userMediaFileEntityArgumentCaptor.capture());
        UserMediaFileEntity savedEntity = userMediaFileEntityArgumentCaptor.getValue();
        assertEquals(userEntity, savedEntity.getUser());
        assertEquals(mediaFileEntity, savedEntity.getMediaFile());
    }

    @Test
    void existsImageByUserId() {
        //given
        Long id = 1L;
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        //when
        underTest.existsImageByUserId(id);
        //then
        verify(userMediaFileRepository).existsByUserId(longArgumentCaptor.capture());
        Long paramId = longArgumentCaptor.getValue();
        assertEquals(id, paramId);
    }
}