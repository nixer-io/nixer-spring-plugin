package eu.xword.nixer.nixerplugin.detection.rules;

import java.util.ArrayList;
import java.util.List;

import eu.xword.nixer.nixerplugin.events.UserAgentFailedLoginOverThresholdEvent;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.inmemory.LoginMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAgentFailedLoginOverThresholdRuleTest {

    private static final int THRESHOLD = 3;
    private static final int UNDER_THRESHOLD = THRESHOLD - 1;
    private static final int OVER_THRESHOLD = THRESHOLD + 1;

    @Mock
    private LoginMetric loginMetric;

    private UserAgentLoginOverThresholdRule rule;

    private static final String UAS_TOKEN = "uas-token";

    @BeforeEach
    void setup() {
        rule = new UserAgentLoginOverThresholdRule(loginMetric);
        rule.setThreshold(THRESHOLD);
    }

    @Test
    void should_emit_event_for_useragent_over_threshold() {
        when(loginMetric.value(UAS_TOKEN)).thenReturn(OVER_THRESHOLD);

        final List<Object> events = execute(UAS_TOKEN);

        assertThat(events).contains(new UserAgentFailedLoginOverThresholdEvent(UAS_TOKEN));
    }

    @Test
    void should_not_emit_event_for_useragent_under_threshold() {
        when(loginMetric.value(UAS_TOKEN)).thenReturn(UNDER_THRESHOLD);

        final List<Object> events = execute(UAS_TOKEN);

        assertThat(events).isEmpty();
    }

    private List<Object> execute(final String userAgentToken) {
        final LoginContext loginContext = new LoginContext();
        loginContext.setUserAgentToken(userAgentToken);
        final List<Object> events = new ArrayList<>();

        rule.execute(loginContext, events::add);
        return events;
    }
}