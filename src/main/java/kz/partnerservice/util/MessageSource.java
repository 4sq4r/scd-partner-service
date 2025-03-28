package kz.partnerservice.util;

public enum MessageSource {

    USER_NOT_FOUND("User not found, username: %s"),
    USERNAME_ALREADY_EXISTS("Username already exists, username: %s"),
    PHONE_NUMBER_EXISTS("Phone number already exists, phone number: %s"),
    WRONG_PHONE_NUMBER_FORMAT("Wrong phone number format"),
    JOB_ALREADY_EXISTS("Job already exists"), JOB_NOT_FOUND("Job not found, id: %s"),
    ACCESS_DENIED("Access denied"),
    MINIO_BUCKET_NOT_EXIST("Minio bucket is not exists, bucket: %s"),
    IMAGE_ALREADY_EXISTS("Image already exists");

    private final String text;

    MessageSource(String text) {
        this.text = text;
    }

    public String getText(String... params) {
        return String.format(this.text, params);
    }
}
