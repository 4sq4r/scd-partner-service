package kz.partnerservice.util;

import java.util.regex.Pattern;

public class StringUtils {

    public static final Pattern PHONE_PATTERN = Pattern.compile("^[87]\\d{10}$");
}
