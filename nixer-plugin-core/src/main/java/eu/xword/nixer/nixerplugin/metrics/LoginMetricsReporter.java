package eu.xword.nixer.nixerplugin.metrics;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import eu.xword.nixer.nixerplugin.blocking.policies.BadCaptchaException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static eu.xword.nixer.nixerplugin.metrics.FailureCategory.BAD_PASSWORD;
import static eu.xword.nixer.nixerplugin.metrics.FailureCategory.DISABLED;
import static eu.xword.nixer.nixerplugin.metrics.FailureCategory.EXPIRED;
import static eu.xword.nixer.nixerplugin.metrics.FailureCategory.INVALID_CAPTCHA;
import static eu.xword.nixer.nixerplugin.metrics.FailureCategory.LOCKED;
import static eu.xword.nixer.nixerplugin.metrics.FailureCategory.OTHER;
import static eu.xword.nixer.nixerplugin.metrics.FailureCategory.UNKNOWN_USER;

public class LoginMetricsReporter implements ApplicationListener<AbstractAuthenticationEvent> {

    private static final Map<Class<? extends AuthenticationException>, FailureCategory> CATEGORY_BY_EXCEPTION;

    private final Counter loginSuccessCounter;

    private final Map<FailureCategory, Counter> failureCounters;

    static {
        CATEGORY_BY_EXCEPTION = ImmutableMap.<Class<? extends AuthenticationException>, FailureCategory>builder()
                .put(BadCredentialsException.class, BAD_PASSWORD)
                .put(UsernameNotFoundException.class, UNKNOWN_USER) // TODO hidden as BadCredentialsException, requires hideUserNotFoundExceptions
                .put(LockedException.class, LOCKED)
                .put(AccountExpiredException.class, EXPIRED)
                .put(DisabledException.class, DISABLED)
                .put(BadCaptchaException.class, INVALID_CAPTCHA) // TODO reported as BAD_PASSWORD
                .build(); // TODO separated exception for credentials and account expired/disabled/locked
    }

    public LoginMetricsReporter(final MeterRegistry meterRegistry) {
        final EnumMap<FailureCategory, Counter> result = Maps.newEnumMap(FailureCategory.class);

        for (FailureCategory it : FailureCategory.values()) {
            final Counter counter = Counter.builder("login")
                    .description("User login failed")
                    .tags("result", "failed")
                    .tag("reason", it.name())
                    .register(meterRegistry);
            result.put(it, counter);
        }
        this.failureCounters = result;

        this.loginSuccessCounter = Counter.builder("login")
                .description("User login succeeded")
                .tags("result", "success")
                .register(meterRegistry);

    }

    private void reportLoginFail(final FailureCategory failureCategory) {
        final Counter failureCounter = failureCounters.get(failureCategory);

        failureCounter.increment();
    }

    private void reportLoginSuccess() {
        loginSuccessCounter.increment();
    }

    @Override
    public void onApplicationEvent(final AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            reportLoginSuccess();
        } else if (event instanceof AbstractAuthenticationFailureEvent) {
            final AuthenticationException exception = ((AbstractAuthenticationFailureEvent) event).getException();
            final FailureCategory category = CATEGORY_BY_EXCEPTION.getOrDefault(exception.getClass(), OTHER);

            reportLoginFail(category);
        }
    }
}
