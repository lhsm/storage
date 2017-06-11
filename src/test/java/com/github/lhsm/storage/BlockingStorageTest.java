package com.github.lhsm.storage;

import net.jodah.concurrentunit.ConcurrentTestCase;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

class BlockingStorageTest extends ConcurrentTestCase {

    private Storage diskStorage = new Storage() {
        @Override
        public byte[] get(String key) {
            resume();
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                threadAssertTrue(false);
            }
            return new byte[0];
        }

        @Override
        public byte[] put(String key, byte[] value) {
            try {
                await(1_000, 1);
            } catch (TimeoutException e) {
                threadAssertTrue(false);
            }

            resume();

            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                threadAssertTrue(false);
            }

            resume();

            return new byte[0];
        }

        @Override
        public byte[] remove(String key) {
            return new byte[0];
        }

        @Override
        public Stream<Storage.Entry> entries() {
            return null;
        }
    };

    private BlockingStorage subj = new BlockingStorage(diskStorage, new KeySpread());

    @Test
    void getShouldNotBlockAnotherGet() throws InterruptedException, TimeoutException {
        final String key = "test";
        new Thread(() -> subj.get(key)).start();
        new Thread(() -> subj.get(key)).start();

        await(500, 2);
    }

    @Test
    void putSholdBlockGet() throws TimeoutException, InterruptedException {
        final String key = "test";
        new Thread(() -> subj.put(key, key.getBytes())).start();
        new Thread(() -> {
            resume();
            subj.get(key);
        }).start();

        Thread.sleep(1_000);

        await(0, 3);
    }

}