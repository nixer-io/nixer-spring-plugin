package io.nixer.nixerplugin.core.login.inmemory;

import java.time.Duration;

import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginFailureType;
import io.nixer.nixerplugin.core.login.LoginResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginCounterTest {

    private static final String IP_1 = "127.0.0.1";
    private static final String IP_2 = "127.0.0.2";

    private final ClockStub clock = new ClockStub();

    private final LoginCounter counter = LoginCounterBuilder.counter(FeatureKey.Features.IP)
            .window(Duration.ofMinutes(1))
            .clock(clock)
            .buildCachedRollingCounter();

    @Test
    void should_increment_counter() {
        counter.onLogin(failedLoginContext(IP_1));

        final int count = counter.value(IP_1);

        assertEquals(1, count);
    }

    @Test
    void should_return_zero_count_for_unseen_key() {
        final int count = counter.value(IP_1);

        assertEquals(0, count);
    }

    @Test
    void should_track_multiple_keys() {
        counter.onLogin(failedLoginContext(IP_1));
        counter.onLogin(failedLoginContext(IP_2));
        counter.onLogin(failedLoginContext(IP_2));

        assertEquals(1, counter.value(IP_1));
        assertEquals(2, counter.value(IP_2));
    }

    @Test
    void should_reset_counter_on_successful_login() {
        counter.onLogin(failedLoginContext(IP_1));
        counter.onLogin(successfulLoginContext(IP_1));

        final int count = counter.value(IP_1);

        assertEquals(0, count);
    }

    @Test
    void should_discard_count_after_expiration() {
        counter.onLogin(failedLoginContext(IP_1));

        clock.tick(Duration.ofMinutes(5));

        final int count = counter.value(IP_1);

        assertEquals(0, count);
    }

    private static LoginContext failedLoginContext(final String ip) {
        final LoginContext loginContext = new LoginContext();
        loginContext.setIpAddress(ip);
        loginContext.setLoginResult(LoginResult.failure(LoginFailureType.BAD_PASSWORD));
        return loginContext;
    }

    private static LoginContext successfulLoginContext(final String ip) {
        final LoginContext loginContext = new LoginContext();
        loginContext.setIpAddress(ip);
        loginContext.setLoginResult(LoginResult.success());
        return loginContext;
    }
}
