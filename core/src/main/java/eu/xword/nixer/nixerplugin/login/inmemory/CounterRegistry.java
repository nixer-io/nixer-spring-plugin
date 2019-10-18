package eu.xword.nixer.nixerplugin.login.inmemory;

import eu.xword.nixer.nixerplugin.login.LoginMetricCounter;

public interface CounterRegistry {
    void registerCounter(final LoginMetricCounter loginMetricCounter);
}
