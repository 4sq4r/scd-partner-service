package kz.partnerservice.configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import kz.partnerservice.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static kz.partnerservice.util.MessageSource.MINIO_BUCKET_NOT_EXIST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Configuration
public class MinioConfiguration {

    @Value("${minio.url}")
    private String url;
    @Value("${minio.bucket}")
    private String bucket;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.connect-timeout}")
    private int connectTimeout;
    @Value("${minio.write-timeout}")
    private int writeTimeout;
    @Value("${minio.read-timeout}")
    private int readTimeout;
    @Value("${minio.auto-create-bucket}")
    private boolean autoCreateBucket;

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient minioClient = new MinioClient.Builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        minioClient.setTimeout(connectTimeout, writeTimeout, readTimeout);
        boolean isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucket)
                .build());

        if (!isBucketExist) {
            if (autoCreateBucket) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build());
            } else {
                throw CustomException.builder()
                        .httpStatus(INTERNAL_SERVER_ERROR)
                        .message(MINIO_BUCKET_NOT_EXIST.getText(bucket))
                        .build();
            }
        }

        return minioClient;
    }
}
