package com.github.lhsm.storage;

import java.util.Arrays;

public class KeyValidator {

    public static final String[] DEFAULT_FORBIDDEN = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|", "+"};

    private final String[] forbiddenChars;

    private final int length;

    public KeyValidator() {
        this(DEFAULT_FORBIDDEN, 128);
    }

    public KeyValidator(String[] forbiddenChars, int length) {
        this.forbiddenChars = forbiddenChars;
        this.length = length;
    }

    public boolean isValid(String key) {
        return key.length() <= length
                && Arrays.stream(forbiddenChars).parallel().noneMatch(key::contains);
    }
}
