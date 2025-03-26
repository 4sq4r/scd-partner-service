package kz.partnerservice.service.impl;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.AuthDTO;
import kz.partnerservice.model.entity.UserEntity;
import kz.partnerservice.repository.UserRepository;
import kz.partnerservice.security.JwtService;
import kz.partnerservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static kz.partnerservice.util.MessageSource.USERNAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public void signUp(AuthDTO authDTO) throws CustomException {
        formatAuthDTO(authDTO);
        checkIfExistsByUsername(authDTO.getUsername());

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(authDTO.getUsername());
        userEntity.setPassword(passwordEncoder.encode(authDTO.getPassword()));
        userRepository.save(userEntity);
    }

    @Override
    public AuthDTO signIn(AuthDTO authDTO) {
        formatAuthDTO(authDTO);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);
        authDTO.setToken(token);

        return authDTO;
    }

    private void formatAuthDTO(AuthDTO authDTO) {
        authDTO.setUsername(authDTO.getUsername().trim());
        authDTO.setPassword(authDTO.getPassword().trim());
    }

    private void checkIfExistsByUsername(String username) throws CustomException {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(USERNAME_ALREADY_EXISTS.getText(username))
                    .build();
        }
    }
}
