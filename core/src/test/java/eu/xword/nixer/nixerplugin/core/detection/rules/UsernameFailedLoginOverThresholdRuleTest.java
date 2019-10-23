package eu.xword.nixer.nixerplugin.core.detection.rules;

import java.util.ArrayList;
import java.util.List;

import eu.xword.nixer.nixerplugin.core.events.UsernameFailedLoginOverThresholdEvent;
import eu.xword.nixer.nixerplugin.core.login.LoginContext;
import eu.xword.nixer.nixerplugin.core.login.inmemory.LoginMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameFailedLoginOverThresholdRuleTest {

    private static final int THRESHOLD = 3;
    private static final int UNDER_THRESHOLD = THRESHOLD - 1;
    private static final int OVER_THRESHOLD = THRESHOLD + 1;

    @Mock
    private LoginMetric loginMetric;

    private UsernameFailedLoginOverThresholdRule rule;

    private static final String USER = "user";

    @BeforeEach
    void setup() {
        rule = new UsernameFailedLoginOverThresholdRule(loginMetric);
        rule.setThreshold(THRESHOLD);
    }

    @Test
    void should_emit_event_for_username_over_threshold() {
        when(loginMetric.value(USER)).thenReturn(OVER_THRESHOLD);

        final List<Object> events = execute(USER);

        assertThat(events).contains(new UsernameFailedLoginOverThresholdEvent(USER));
    }

    @Test
    void should_not_emit_event_for_user_under_threshold() {
        when(loginMetric.value(USER)).thenReturn(UNDER_THRESHOLD);

        final List<Object> events = execute(USER);

        assertThat(events).isEmpty();
    }

    private List<Object> execute(final String username) {
        final LoginContext loginContext = new LoginContext();
        loginContext.setUsername(username);
        final List<Object> events = new ArrayList<>();

        rule.execute(loginContext, events::add);
        return events;
    }
}