package org.makrowave.agartha_plinko_backend.shared.util;

import java.util.regex.Pattern;

public class Validation {
    public static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
}
