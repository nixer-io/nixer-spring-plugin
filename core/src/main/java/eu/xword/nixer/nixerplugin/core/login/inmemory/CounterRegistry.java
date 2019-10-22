package eu.xword.nixer.nixerplugin.core.login.inmemory;

import eu.xword.nixer.nixerplugin.core.login.LoginMetricCounter;

public interface CounterRegistry {
    void registerCounter(final LoginMetricCounter loginMetricCounter);
}
