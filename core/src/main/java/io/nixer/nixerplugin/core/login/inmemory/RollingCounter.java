package io.nixer.nixerplugin.core.login.inmemory;

/**
 * Abstraction for rolling counter aka sliding window counter for multiple keys.
 */
public interface RollingCounter {

    void increment(String key);

    void remove(String key);

    int count(String key);
}
