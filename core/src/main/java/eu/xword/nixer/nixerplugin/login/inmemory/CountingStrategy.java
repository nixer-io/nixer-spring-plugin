package eu.xword.nixer.nixerplugin.login.inmemory;

import java.util.function.Consumer;

import eu.xword.nixer.nixerplugin.login.LoginResult;

/**
 * Abstraction for strategy factor for metric counting
 */
interface CountingStrategy {
    CountFunction count(final RollingCounter counter, final LoginResult result);

    interface CountFunction extends Consumer<String> {

    }
}
