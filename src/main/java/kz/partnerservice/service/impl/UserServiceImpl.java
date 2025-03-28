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
import kz.partnerservice.service.UserService;
import kz.partnerservice.util.MessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static kz.partnerservice.util.MessageSource.PHONE_NUMBER_EXISTS;
import static kz.partnerservice.util.MessageSource.WRONG_PHONE_NUMBER_FORMAT;
import static kz.partnerservice.util.StringUtils.MINIO_FILE_FORMAT;
import static kz.partnerservice.util.StringUtils.PHONE_PATTERN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final MinioService minioService;
    private final MediaFileServiceImpl mediaFileService;
    private final UserMediaFileServiceImpl userMediaFileService;
    private final MediaFileMapper mediaFileMapper;

    @Value("${minio.folder-names.users}")
    private String FOLDER_NAME;

    @Value("${minio.actual-minio-urls.users}")
    private String ACTUAL_MINIO_URL;

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

    public UserEntity getUserEntityFromContext() throws CustomException {
        String username = jwtService.getUsername();

        return userRepository.findByUsernameIgnoreCase(username).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(BAD_REQUEST)
                        .message(MessageSource.USER_NOT_FOUND.getText(username))
                        .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaFileDTO uploadImage(MultipartFile file) throws CustomException {
        UserEntity userEntity = getUserEntityFromContext();

        if (userMediaFileService.existsImageByUserId(userEntity.getId())) {
            throw CustomException.builder()
                    .httpStatus(BAD_REQUEST)
                    .message(MessageSource.IMAGE_ALREADY_EXISTS.getText())
                    .build();
        }

        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        UUID uuid = randomUUID();
        String fileName = uuid + "." + split[split.length - 1];
        String url = MINIO_FILE_FORMAT.formatted(FOLDER_NAME, fileName);
        minioService.uploadMediaFile(file, MINIO_FILE_FORMAT.formatted(ACTUAL_MINIO_URL, fileName));
        MediaFileEntity mediaFileEntity = mediaFileService.saveOne(url);
        userMediaFileService.saveOne(userEntity, mediaFileEntity);

        return mediaFileMapper.toDTO(mediaFileEntity);
    }

    private Optional<UserEntity> findEntityByPhoneNumber(Long phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    private void validatePhoneNumber(Long userId, Long phoneNumber) throws CustomException {
        if (!PHONE_PATTERN.matcher(phoneNumber.toString()).matches()) {
            throw CustomException.builder()
                    .httpStatus(BAD_REQUEST)
                    .message(WRONG_PHONE_NUMBER_FORMAT.getText())
                    .build();
        }

        Optional<UserEntity> optionalUserEntity = findEntityByPhoneNumber(phoneNumber);

        if (optionalUserEntity.isPresent() && !optionalUserEntity.get().getId().equals(userId)) {
            throw CustomException.builder()
                    .httpStatus(BAD_REQUEST)
                    .message(PHONE_NUMBER_EXISTS.getText(phoneNumber.toString()))
                    .build();
        }
    }

    @Override
    public UserDTO getInfo() throws CustomException {
        UserEntity userEntity = getUserEntityFromContext();
        UserDTO userDTO = userMapper.toDTO(userEntity);
        userDTO.setMediaFile(mediaFileMapper.toDTO(userEntity.getMediaFile().getMediaFile()));

        return userDTO;
    }
}
