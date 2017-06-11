package com.github.lhsm.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KeyValidatorTest {

    private KeyValidator subj = new KeyValidator();

    @Test
    void isValid() {
        Assertions.assertTrue(subj.isValid("test"));
        Assertions.assertTrue(subj.isValid("test.tt.yty"));
    }

    @Test
    void isNotValid() {
        Assertions.assertFalse(subj.isValid("test/"));
        Assertions.assertFalse(subj.isValid("test*"));
    }

    @Test
    void shouldCheckLength() {
        Assertions.assertFalse(new KeyValidator(KeyValidator.DEFAULT_FORBIDDEN, 5).isValid("123456"));
    }

}