package eu.xword.nixer.nixerplugin.login.inmemory;

import java.util.function.Consumer;

import eu.xword.nixer.nixerplugin.login.LoginResult;

/**
 * Abstraction for strategy factor for metric counting
 */
public interface CountingStrategy {
    CounterFunction counter(final RollingCounter counter, final LoginResult result);

    interface CounterFunction extends Consumer<String> {

    }
}
