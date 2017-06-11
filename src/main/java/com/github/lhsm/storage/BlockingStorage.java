package com.github.lhsm.storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

/**
 * Implements key-value storage with multiple threads support. Uses read-write lock to guard for internal store.
 */
public class BlockingStorage implements Storage {

    private final KeySpread spread;
    private final Storage storage;
    private final ConcurrentHashMap<Integer, ReadWriteLock> locks = new ConcurrentHashMap<>();

    public BlockingStorage(Storage storage, KeySpread spread) {
        this.spread = spread;
        this.storage = storage;
    }

    @Override
    public byte[] get(String key) {
        Lock lock = getLock(key).readLock();
        try {
            lock.lock();
            return storage.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte[] put(String key, byte[] value) {
        Lock lock = getLock(key).writeLock();
        try {
            lock.lock();
            return storage.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte[] remove(String key) {
        return put(key, null);
    }

    @Override
    public Stream<Entry> entries() {
        return storage.entries();
    }

    private ReadWriteLock getLock(String key) {
        return locks.computeIfAbsent(spread.hash(key), integer -> new ReentrantReadWriteLock());
    }

}
