package com.github.lhsm.storage;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KeySpreadTest {

    @ParameterizedTest
    @CsvSource({
            "111, 1, 0",
            "111111, 1, 1",
            "11111111111, 1, 0",
            "11111111111111111, 1, 0",
            "11111111111111111111111111111, 1, -1",
            "111111, 2, 2",
            "11111111111111111, 2, 1",
            "111111111111111111111111, 2, 0",
            "1, 10, 0",
            "Z111, 10, 1",
            "ЯZ11, 1000, 3",
            "111, 20, 23",
            "111, 16, 1"
    })
    void hash(String key, int level, int range) {
        KeySpread spread = new KeySpread(level);
        MatcherAssert.assertThat(spread.hash(key), CoreMatchers.is(range));
    }
}