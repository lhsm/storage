package com.github.lhsm.storage;

import java.util.stream.Stream;

/**
 * Represents key-value (string to byte array) storage
 */
public interface Storage {

    /**
     * Gets value by key
     *
     * @param key string key
     * @return value
     * @throws StorageException
     */
    byte[] get(String key);

    /**
     * Stores value for given key
     *
     * @param key   string key
     * @param value byte array value
     * @return previous value or null
     * @throws StorageException
     */
    byte[] put(String key, byte[] value);

    /**
     * Removes value for key
     *
     * @param key strimng key
     * @return removed value
     * @throws StorageException
     */
    byte[] remove(String key);

    /**
     * Gets stream of strage entries
     *
     * @return stream of storage entries
     * @throws StorageException
     */
    Stream<Entry> entries();

    interface Entry {

        String getKey();

        byte[] getValue();
    }
}
