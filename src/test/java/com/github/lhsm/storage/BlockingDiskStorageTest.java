package com.github.lhsm.storage;

class BlockingDiskStorageTest extends DiskStorageTest {

    private Storage subj = new BlockingStorage(new DiskStorage(ROOT, TMP, keyValidator), new KeySpread());

    @Override
    protected Storage getStorage() {
        return subj;
    }
}