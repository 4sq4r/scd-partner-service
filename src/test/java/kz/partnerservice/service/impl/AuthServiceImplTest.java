package kz.partnerservice.service.impl;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.AuthDTO;
import kz.partnerservice.model.entity.UserEntity;
import kz.partnerservice.repository.UserRepository;
import kz.partnerservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static kz.partnerservice.util.MessageSource.USERNAME_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String TOKEN = "token";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl underTest;

    private AuthDTO authDTO;

    @BeforeEach
    void setUp() {
        authDTO = new AuthDTO();
        authDTO.setUsername(USERNAME);
        authDTO.setPassword(PASSWORD);
    }

    @Test
    void signUp_ShouldSaveUser_WhenUsernameIsUnique() throws CustomException {
        // given
        ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.existsByUsernameIgnoreCase(authDTO.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(authDTO.getPassword())).thenReturn(ENCODED_PASSWORD);

        //when
        underTest.signUp(authDTO);

        //then
        verify(userRepository, times(1)).save(userEntityArgumentCaptor.capture());
        UserEntity savedUserEntity = userEntityArgumentCaptor.getValue();
        assertNotEquals(authDTO.getPassword(), savedUserEntity.getPassword());
    }

    @Test
    void signUp_ShouldThrowException_WhenUsernameExists() {
        // given
        when(userRepository.existsByUsernameIgnoreCase(authDTO.getUsername())).thenReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.signUp(authDTO));

        // then
        assertEquals(BAD_REQUEST, exception.getHttpStatus());
        assertEquals(USERNAME_ALREADY_EXISTS.getText(authDTO.getUsername()), exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void signIn_ShouldAuthenticateUserAndReturnToken() {
        // given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn(TOKEN);

        //when
        AuthDTO result = underTest.signIn(authDTO);

        //then
        assertEquals(TOKEN, result.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(authentication);
    }
}