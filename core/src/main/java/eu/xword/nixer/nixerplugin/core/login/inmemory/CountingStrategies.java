package eu.xword.nixer.nixerplugin.core.login.inmemory;

import eu.xword.nixer.nixerplugin.core.login.LoginResult;

/**
 * Factories for counting strategies
 */
public enum CountingStrategies implements CountingStrategy {

    CONSECUTIVE_FAILS {
        @Override
        public CounterFunction counter(final RollingCounter counter, final LoginResult result) {
            return result.isSuccess() ? counter::remove : counter::increment;
        }
    },
    TOTAL_FAILS {
        @Override
        public CounterFunction counter(final RollingCounter counter, final LoginResult result) {
            return result.isSuccess() ? NOP : counter::increment;
        }
    };

    public static final CounterFunction NOP = (it) -> {
    };

}
