package eu.xword.nixer.nixerplugin.core.login.inmemory;

import java.util.function.Consumer;

import eu.xword.nixer.nixerplugin.core.login.LoginResult;

/**
 * Abstraction for strategy factor for metric counting
 */
public interface CountingStrategy {
    CounterFunction counter(final RollingCounter counter, final LoginResult result);

    interface CounterFunction extends Consumer<String> {

    }
}
