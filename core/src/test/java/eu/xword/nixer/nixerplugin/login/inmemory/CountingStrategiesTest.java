package eu.xword.nixer.nixerplugin.login.inmemory;

import eu.xword.nixer.nixerplugin.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CountingStrategiesTest {

    private static final String KEY = "key";

    LoginResult success = LoginResult.success();
    LoginResult failed = LoginResult.failure(LoginFailureType.BAD_PASSWORD);

    @Mock
    RollingCounter rollingCounter;

    @Nested
    class TotalFailsStrategyTest {

        @Test
        void shouldCountFails() {
            final CountingStrategies factory = CountingStrategies.TOTAL_FAILS;

            factory.count(rollingCounter, failed).accept(KEY);

            verify(rollingCounter).increment(KEY);
            verifyNoMoreInteractions(rollingCounter);

            factory.count(rollingCounter, success).accept(KEY);

            verifyNoMoreInteractions(rollingCounter);
        }
    }

    @Nested
    class ConsecutiveFailsStrategyTest {

        @Test
        void shouldCountConsecutiveFails() {
            final CountingStrategies factory = CountingStrategies.CONSECUTIVE_FAILS;

            factory.count(rollingCounter, failed).accept(KEY);

            verify(rollingCounter).increment(KEY);
            verifyNoMoreInteractions(rollingCounter);

            factory.count(rollingCounter, failed).accept(KEY);
            verify(rollingCounter, times(2)).increment(KEY);
            verifyNoMoreInteractions(rollingCounter);

            factory.count(rollingCounter, success).accept(KEY);
            verify(rollingCounter).remove(KEY);
            verifyNoMoreInteractions(rollingCounter);
        }
    }
}