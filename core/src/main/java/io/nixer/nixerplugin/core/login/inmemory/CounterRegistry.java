package io.nixer.nixerplugin.core.login.inmemory;

import io.nixer.nixerplugin.core.login.LoginMetricCounter;
import io.nixer.nixerplugin.core.login.LoginMetricCounter;

public interface CounterRegistry {
    void registerCounter(final LoginMetricCounter loginMetricCounter);
}
