package eu.xword.nixer.nixerplugin.login.inmemory;

/**
 * Abstraction for rolling counter aka sliding window counter for multiple keys.
 */
public interface RollingCounter {

    void increment(String key);

    void add(String key, int increment);

    void remove(String key);

    int get(String key);
}
