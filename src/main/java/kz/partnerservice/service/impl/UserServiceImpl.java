package kz.partnerservice.service.impl;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.mapper.UserMapper;
import kz.partnerservice.model.dto.UserDTO;
import kz.partnerservice.model.entity.UserEntity;
import kz.partnerservice.repository.UserRepository;
import kz.partnerservice.security.JwtService;
import kz.partnerservice.service.UserService;
import kz.partnerservice.util.MessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kz.partnerservice.util.StringUtils.PHONE_PATTERN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public UserDTO updateOne(UserDTO userDTO) throws CustomException {
        userDTO.setFirstName(userDTO.getFirstName().trim());
        userDTO.setLastName(userDTO.getLastName().trim());
        userDTO.setAddress(userDTO.getAddress().trim());

        UserEntity userEntity = getUserEntityFromContext();
        validatePhoneNumber(userEntity.getId(), userDTO.getPhoneNumber());
        userEntity.setFirstName(userDTO.getFirstName());
        userEntity.setLastName(userDTO.getLastName());
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setPhoneNumber(userDTO.getPhoneNumber());
        userRepository.save(userEntity);

        return userMapper.toDTO(userEntity);
    }

    @Transactional(readOnly = true)
    public UserEntity getUserEntityFromContext() throws CustomException {
        String username = jwtService.getUsername();

        return userRepository.findByUsernameIgnoreCase(username).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(BAD_REQUEST)
                        .message(MessageSource.USER_NOT_FOUND.getText(username))
                        .build());
    }

    private Optional<UserEntity> findEntityByPhoneNumber(Long phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    private void validatePhoneNumber(Long userId, Long phoneNumber) throws CustomException {
        if (!PHONE_PATTERN.matcher(phoneNumber.toString()).matches()) {
            throw CustomException.builder()
                    .httpStatus(BAD_REQUEST)
                    .message(MessageSource.WRONG_PHONE_NUMBER_FORMAT.getText())
                    .build();
        }

        Optional<UserEntity> optionalUserEntity = findEntityByPhoneNumber(phoneNumber);

        if (optionalUserEntity.isPresent() && !optionalUserEntity.get().getId().equals(userId)) {
            throw CustomException.builder()
                    .httpStatus(BAD_REQUEST)
                    .message(MessageSource.PHONE_NUMBER_EXISTS.getText(phoneNumber.toString()))
                    .build();
        }
    }
}
