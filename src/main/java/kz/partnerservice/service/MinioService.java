package kz.partnerservice.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import kz.partnerservice.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private static final long PART_SIZE = 5242880;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public void uploadMediaFile(MultipartFile file, String url) throws CustomException {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(file.getInputStream(), file.getSize(), PART_SIZE)
                    .contentType(file.getContentType())
                    .bucket(bucket)
                    .object(url)
                    .build());
        } catch (Exception e) {
            log.error("Unable to upload media file: {}", e.getMessage());
            throw CustomException.builder()
                    .httpStatus(INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    public void deleteMediaFile(String url) throws CustomException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(url)
                    .build());
        } catch (Exception e) {
            log.error("Unable to delete media file: {}", e.getMessage());
            throw CustomException.builder()
                    .httpStatus(INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }
}
