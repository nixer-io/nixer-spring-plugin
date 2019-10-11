package eu.xword.nixer.nixerplugin.login.counts;

/**
 * Exposes login metric value per given key
 */
public interface LoginMetric {

    //todo should return double ?
    int value(String key);
}
