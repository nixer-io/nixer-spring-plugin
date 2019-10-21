package eu.xword.nixer.nixerplugin.login.inmemory;

/**
 * Exposes login metric value per given key
 */
public interface LoginMetric {

    int value(final String key);
}
