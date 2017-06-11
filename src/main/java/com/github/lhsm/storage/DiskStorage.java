package com.github.lhsm.storage;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

/**
 * Implements key-value storage over file system: file name as key, file data as value
 */
public class DiskStorage implements Storage {

    private final String root;
    private final String tmp;
    private final KeyValidator keyValidator;

    public DiskStorage(String root, String tmp, KeyValidator keyValidator) {
        this.root = root;
        this.tmp = tmp;
        this.keyValidator = keyValidator;
    }

    @Override
    public byte[] get(String key) {
        checkKey(key);

        Path path = Paths.get(root, key);
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            fileChannel.lock(0, Long.MAX_VALUE, true);
            byte[] value = Files.readAllBytes(path);
            return value.length > 0 ? value : null;
        } catch (NoSuchFileException e) {
            try {
                path.toFile().createNewFile();
                return null;
            } catch (IOException e1) {
                throw new StorageException(e1);
            }
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public byte[] put(String key, byte[] value) {
        checkKey(key);

        byte[] prev = get(key);

        try (FileChannel fileChannel = FileChannel.open(Paths.get(root, key), StandardOpenOption.WRITE)) {
            fileChannel.lock(0, Long.MAX_VALUE, false);
            Path temp = Files.createTempFile(Paths.get(tmp), key, key);
            if (value != null) {
                Files.write(temp, value, StandardOpenOption.WRITE);
            }
            Files.move(temp, Paths.get(root, key), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e);
        }

        return prev;
    }

    @Override
    public byte[] remove(String key) {
        return put(key, null);
    }

    @Override
    public Stream<Entry> entries() {
        try {
            return Files.list(Paths.get(root))
                    .filter(path -> !path.equals(Paths.get(tmp)))
                    .map(path -> new DiskEntry(path, this));
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    private void checkKey(String key) {
        if (!keyValidator.isValid(key)) {
            throw new StorageException(new IllegalArgumentException("Invalid key " + key));
        }
    }

    public static class DiskEntry implements Entry {

        private final Path path;

        private final Storage storage;

        DiskEntry(Path path, Storage storage) {
            this.path = path;
            this.storage = storage;
        }

        @Override
        public String getKey() {
            return path.getFileName().toString();
        }

        @Override
        public byte[] getValue() {
            return storage.get(getKey());
        }

        @Override
        public String toString() {
            return getKey();
        }
    }

}
