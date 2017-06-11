package com.github.lhsm.storage;

public class KeySpread {

    private final int range;

    public KeySpread() {
        this(16);
    }

    public KeySpread(int level) {
        this.range = Integer.MAX_VALUE >>> level;
    }

    public int hash(String key) {
        return key.hashCode() / range;
    }
}
