package kz.partnerservice.service.impl;

import kz.partnerservice.model.entity.MediaFileEntity;
import kz.partnerservice.repository.MediaFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MediaFileServiceImplTest {

    @Mock
    private MediaFileRepository mediaFileRepository;

    @InjectMocks
    private MediaFileServiceImpl underTest;

    @Test
    void saveOne_savesMediaFile() {
        //given
        ArgumentCaptor<MediaFileEntity> mediaFileEntityArgumentCaptor = ArgumentCaptor.forClass(MediaFileEntity.class);
        //when
        underTest.saveOne("url");
        //then
        verify(mediaFileRepository).save(mediaFileEntityArgumentCaptor.capture());
        MediaFileEntity savedEntity = mediaFileEntityArgumentCaptor.getValue();
        assertNotNull(savedEntity);
        assertEquals("url", savedEntity.getUrl());
    }
}