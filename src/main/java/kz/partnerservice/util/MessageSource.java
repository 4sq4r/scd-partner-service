package kz.partnerservice.util;

public enum MessageSource {

    USER_NOT_FOUND("User not found, username: %s"),
    USERNAME_ALREADY_EXISTS("Username already exists, username: %s"),
    PHONE_NUMBER_EXISTS("Phone number already exists, phone number: %s"),
    WRONG_PHONE_NUMBER_FORMAT("Wrong phone number format");

    private final String text;

    MessageSource(String text) {
        this.text = text;
    }

    public String getText(String... params) {
        return String.format(this.text, params);
    }
}
