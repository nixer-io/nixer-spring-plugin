package eu.xword.nixer.nixerplugin.login.inmemory;

import eu.xword.nixer.nixerplugin.login.counts.LoginCounter;

public interface CounterRegistry {
    void registerCounter(final LoginCounter loginCounter);
}
