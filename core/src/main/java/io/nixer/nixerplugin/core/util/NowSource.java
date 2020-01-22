package io.nixer.nixerplugin.core.util;

import java.time.Instant;

/**
 * Abstraction over providing current system time.
 * Wraps the core JDK methods in order to improve testability and visibility.
 * <br>
 * To be used as a service, e.g. bean, by components requiring the current moment in time.
 *
 * Created on 22/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class NowSource {

    /**
     * See {@link Instant#now()}
     */
    public Instant now() {
        return Instant.now();
    }

    /**
     * @see System#currentTimeMillis()
     */
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
