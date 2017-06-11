package com.github.lhsm.storage;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

class DiskStorageTest {

    final static String ROOT = "./storage";
    final static String TMP = "./storage/tmp";
    KeyValidator keyValidator = Mockito.mock(KeyValidator.class);

    private DiskStorage subj = new DiskStorage(ROOT, TMP, keyValidator);

    protected Storage getStorage() {
        return subj;
    }

    @BeforeEach
    void setUp() throws IOException {
        if (Files.notExists(Paths.get(ROOT))) {
            Files.createDirectory(Paths.get(ROOT));
        }

        if (Files.notExists(Paths.get(TMP))) {
            Files.createDirectory(Paths.get(TMP));
        }

        Mockito.when(keyValidator.isValid(Mockito.anyString())).thenReturn(true);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(Paths.get(ROOT))) {
            FileUtils.forceDelete(Paths.get(ROOT).toFile());
        }
        if (Files.exists(Paths.get(TMP))) {
            FileUtils.forceDelete(Paths.get(TMP).toFile());
        }
    }

    @Test
    void get() {
        assertPutGet("test", "test");
        assertPutGet("test", "test1");
        assertPutGet("tes1t", "test1");
    }

    @Test
    void put() {
        final String test = "test";
        Assertions.assertNull(getStorage().put(test, test.getBytes()));
        byte[] value = getStorage().put(test, test.getBytes());
        Assertions.assertNotNull(value);
        MatcherAssert.assertThat(new String(value, StandardCharsets.UTF_8), CoreMatchers.is(test));
    }

    @Test
    void remove() {
        assertPutGet("test", "test");
        assertRemove("test", "test");
    }

    @Test
    void entries() {
        final String[] keys = new String[]{"test", "test1", "test2"};

        for (String _key : keys) {
            getStorage().put(_key, _key.getBytes());
        }

        MatcherAssert.assertThat(getStorage().entries().map(Storage.Entry::getKey).toArray(), CoreMatchers.is(keys));
        MatcherAssert.assertThat(
                getStorage().entries()
                        .map(_entry -> new String(_entry.getValue(), StandardCharsets.UTF_8))
                        .toArray(),
                CoreMatchers.is(keys)
        );
    }

    @Test
    void invalidKey() {
        Mockito.when(keyValidator.isValid(Mockito.anyString())).thenReturn(false);
        Assertions.assertThrows(StorageException.class, () -> getStorage().get("invalidkey"));
    }


    private void assertPutGet(String key, String value) {
        getStorage().put(key, value.getBytes());
        MatcherAssert.assertThat(new String(getStorage().get(key), StandardCharsets.UTF_8), CoreMatchers.is(value));
    }

    private void assertRemove(String key, String value) {
        MatcherAssert.assertThat(new String(getStorage().remove(key), StandardCharsets.UTF_8), CoreMatchers.is(value));
        Assertions.assertNull(getStorage().get(key));
    }
}