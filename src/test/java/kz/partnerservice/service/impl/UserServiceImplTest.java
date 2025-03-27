package kz.partnerservice.service.impl;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.mapper.UserMapper;
import kz.partnerservice.model.dto.UserDTO;
import kz.partnerservice.model.entity.UserEntity;
import kz.partnerservice.repository.UserRepository;
import kz.partnerservice.security.JwtService;
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

import static kz.partnerservice.util.MessageSource.PHONE_NUMBER_EXISTS;
import static kz.partnerservice.util.MessageSource.WRONG_PHONE_NUMBER_FORMAT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String USERNAME = "username";

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserServiceImpl underTest;

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
}