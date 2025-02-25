package kz.demo.util;

public enum MessageSource {

    COMPANY_NOT_FOUND("Company not found, id: %s"),
    WRONG_PHONE_FORMAT("Wrong phone format, phone: %s"),
    COMPANY_PHONE_EXISTS("Company phone already exists, phone: %s"),
    COMPANY_NAME_EXISTS("Company name already exists, name: %s"),
    ;

    private final String text;

    MessageSource(String text) {
        this.text = text;
    }

    public String getText(String... params) {
        return String.format(this.text, params);
    }
}
