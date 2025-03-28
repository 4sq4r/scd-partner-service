package kz.partnerservice.service.impl;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.mapper.MediaFileMapper;
import kz.partnerservice.mapper.UserMapper;
import kz.partnerservice.model.dto.MediaFileDTO;
import kz.partnerservice.model.dto.UserDTO;
import kz.partnerservice.model.entity.MediaFileEntity;
import kz.partnerservice.model.entity.UserEntity;
import kz.partnerservice.repository.UserRepository;
import kz.partnerservice.security.JwtService;
import kz.partnerservice.service.MinioService;
import kz.partnerservice.util.MessageSource;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static kz.partnerservice.util.MessageSource.PHONE_NUMBER_EXISTS;
import static kz.partnerservice.util.MessageSource.WRONG_PHONE_NUMBER_FORMAT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String USERNAME = "username";
    private static final MultipartFile file = new MockMultipartFile("data", "filename.jpg",
            "text/plain", "some xml".getBytes());

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserMediaFileServiceImpl userMediaFileService;
    @Mock
    private MinioService minioService;
    @Mock
    private MediaFileServiceImpl mediaFileService;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Spy
    private MediaFileMapper mediaFileMapper = Mappers.getMapper(MediaFileMapper.class);

    @InjectMocks
    private UserServiceImpl underTest;

    @BeforeEach
    void initValues() {
        ReflectionTestUtils.setField(underTest, "FOLDER_NAME", "/s3/partner-service/users");
        ReflectionTestUtils.setField(underTest, "ACTUAL_MINIO_URL", "/users");
    }

    @Test
    void updateOne_throwsException_whenUserNotFoundByUsername() {
        //given
        when(jwtService.getUsername()).thenReturn(USERNAME);
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(Instancio.create(UserDTO.class)));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.USER_NOT_FOUND.getText(USERNAME), e.getMessage());
    }

    @Test
    void updateOne_throwsException_whenWrongPhoneNumberFormat_greaterThan_11() {
        //given
        UserDTO userDTO = Instancio.create(UserDTO.class);
        userDTO.setPhoneNumber(3000012312300L);
        UserEntity userEntity = Instancio.create(UserEntity.class);

        when(jwtService.getUsername()).thenReturn(USERNAME);
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(Optional.of(userEntity));
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(userDTO));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(WRONG_PHONE_NUMBER_FORMAT.getText(), e.getMessage());
    }

    @Test
    void updateOne_throwsException_whenWrongPhoneNumberFormat_shorterThan_11() {
        //given
        UserDTO userDTO = Instancio.create(UserDTO.class);
        userDTO.setPhoneNumber(7000000L);
        UserEntity userEntity = Instancio.create(UserEntity.class);

        when(jwtService.getUsername()).thenReturn(USERNAME);
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(Optional.of(userEntity));
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(userDTO));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(WRONG_PHONE_NUMBER_FORMAT.getText(), e.getMessage());
    }

    @Test
    void updateOne_throwsException_whenWrongPhoneNumberFormat_notStartsAt_7() {
        //given
        UserDTO userDTO = Instancio.create(UserDTO.class);
        userDTO.setPhoneNumber(61111111111L);
        UserEntity userEntity = Instancio.create(UserEntity.class);

        when(jwtService.getUsername()).thenReturn(USERNAME);
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(Optional.of(userEntity));
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(userDTO));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(WRONG_PHONE_NUMBER_FORMAT.getText(), e.getMessage());
    }

    @Test
    void updateOne_throwsException_whenPhoneNumberExists() {
        //given
        UserDTO userDTO = Instancio.create(UserDTO.class);
        userDTO.setPhoneNumber(71111111111L);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        UserEntity wrongUser = new UserEntity();
        wrongUser.setId(2L);

        when(jwtService.getUsername()).thenReturn(USERNAME);
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(Optional.of(userEntity));
        when(userRepository.findByPhoneNumber(userDTO.getPhoneNumber())).thenReturn(Optional.of(wrongUser));
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.updateOne(userDTO));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(PHONE_NUMBER_EXISTS.getText(userDTO.getPhoneNumber().toString()), e.getMessage());
    }

    @Test
    void updateOne_updatesUserInfo() throws CustomException {
        //given
        ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        UserDTO userDTO = Instancio.create(UserDTO.class);
        userDTO.setPhoneNumber(71111111111L);
        UserEntity userEntity = Instancio.create(UserEntity.class);

        when(jwtService.getUsername()).thenReturn(USERNAME);
        when(userRepository.findByUsernameIgnoreCase(USERNAME)).thenReturn(Optional.of(userEntity));
        when(userRepository.findByPhoneNumber(userDTO.getPhoneNumber())).thenReturn(Optional.empty());
        //when
        UserDTO result = underTest.updateOne(userDTO);
        //then
        verify(userRepository).save(userEntityArgumentCaptor.capture());
        UserEntity savedUserEntity = userEntityArgumentCaptor.getValue();
        assertEquals(userDTO.getFirstName(), savedUserEntity.getFirstName());
        assertEquals(userDTO.getLastName(), savedUserEntity.getLastName());
        assertEquals(userDTO.getPhoneNumber(), savedUserEntity.getPhoneNumber());
        assertEquals(userDTO.getAddress(), savedUserEntity.getAddress());

        assertNotNull(result);
        assertEquals(userDTO.getFirstName(), result.getFirstName());
        assertEquals(userDTO.getLastName(), result.getLastName());
        assertEquals(userDTO.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(userDTO.getAddress(), result.getAddress());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void getInfo_throwsException_whenUserNotFound() {
        //given
        when(jwtService.getUsername()).thenReturn("user");
        when(userRepository.findByUsernameIgnoreCase(anyString())).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.getInfo());
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.USER_NOT_FOUND.getText("user"), e.getMessage());
    }

    @Test
    void getInfo_returnsInfo() throws CustomException {
        //given
        UserEntity userEntity = Instancio.create(UserEntity.class);
        when(jwtService.getUsername()).thenReturn(userEntity.getUsername());
        when(userRepository.findByUsernameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
        //when
        UserDTO result = underTest.getInfo();
        //then
        assertEquals(userEntity.getId(), result.getId());
        assertEquals(userEntity.getUsername(), result.getUsername());
        assertEquals(userEntity.getFirstName(), result.getFirstName());
        assertEquals(userEntity.getLastName(), result.getLastName());
        assertEquals(userEntity.getAddress(), result.getAddress());
        assertEquals(userEntity.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(userEntity.getMediaFile().getMediaFile().getUrl(), result.getMediaFile().getUrl());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void uploadImage_throwsException_whenUserNotFound() {
        //given
        when(jwtService.getUsername()).thenReturn("user");
        when(userRepository.findByUsernameIgnoreCase(anyString())).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.uploadImage(file));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.USER_NOT_FOUND.getText("user"), e.getMessage());
    }

    @Test
    void uploadImage_throwsException_whenUserAlreadyHaveImage() {
        //given
        UserEntity userEntity = Instancio.create(UserEntity.class);
        when(jwtService.getUsername()).thenReturn(userEntity.getUsername());
        when(userRepository.findByUsernameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
        when(userMediaFileService.existsImageByUserId(userEntity.getId())).thenReturn(true);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.uploadImage(file));
        //then
        assertNotNull(e);
        assertEquals(BAD_REQUEST, e.getHttpStatus());
        assertEquals(MessageSource.IMAGE_ALREADY_EXISTS.getText(), e.getMessage());
    }

    @Test
    void uploadImage_uploadsImage() throws CustomException {
        UUID mockUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
            //given
            mockedUuid.when(UUID::randomUUID).thenReturn(mockUuid);
            UserEntity userEntity = Instancio.create(UserEntity.class);
            String filename = UUID.randomUUID() + ".jpg";
            String url = "%s/%s".formatted("/s3/partner-service/users", filename);
            MediaFileEntity mediaFileEntity = Instancio.create(MediaFileEntity.class);
            mediaFileEntity.setUrl(url);
            when(jwtService.getUsername()).thenReturn(userEntity.getUsername());
            when(userRepository.findByUsernameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
            when(userMediaFileService.existsImageByUserId(userEntity.getId())).thenReturn(false);
            when(mediaFileService.saveOne(url)).thenReturn(mediaFileEntity);
            //when
            MediaFileDTO result = underTest.uploadImage(file);
            //then
            verify(minioService).uploadMediaFile(file, "%s/%s".formatted("/users", filename));
            verify(mediaFileService).saveOne(url);
            verify(userMediaFileService).saveOne(userEntity, mediaFileEntity);

            assertNotNull(result);
            assertEquals(url, result.getUrl());
        }
    }
}