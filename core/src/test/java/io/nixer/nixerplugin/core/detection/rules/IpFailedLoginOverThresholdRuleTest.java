package io.nixer.nixerplugin.core.detection.rules;

import java.util.ArrayList;
import java.util.List;

import io.nixer.nixerplugin.core.detection.events.IpFailedLoginOverThresholdEvent;
import io.nixer.nixerplugin.core.detection.rules.threshold.IpFailedLoginOverThresholdRule;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.inmemory.LoginMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpFailedLoginOverThresholdRuleTest {

    private static final int THRESHOLD = 3;
    private static final int UNDER_THRESHOLD = THRESHOLD - 1;
    private static final int OVER_THRESHOLD = THRESHOLD + 1;

    @Mock
    private LoginContext loginContext;

    @Mock
    private LoginMetric loginMetric;

    private IpFailedLoginOverThresholdRule rule;

    @BeforeEach
    void setup() {
        rule = new IpFailedLoginOverThresholdRule(loginMetric);
        rule.setThreshold(THRESHOLD);
    }

    @Test
    void should_emit_event_for_ip_over_threshold() {
        final String ip = "127.0.0.1";
        when(loginMetric.value(ip)).thenReturn(OVER_THRESHOLD);

        final List<Object> events = execute(ip);

        assertThat(events).contains(new IpFailedLoginOverThresholdEvent(ip));
    }

    @Test
    void should_not_emit_event_for_ip_under_threshold() {
        final String ip = "127.0.0.1";
        when(loginMetric.value(ip)).thenReturn(UNDER_THRESHOLD);

        final List<Object> events = execute(ip);

        assertThat(events).isEmpty();
    }

    private List<Object> execute(final String ip) {
        given(loginContext.getIpAddress()).willReturn(ip);
        final List<Object> events = new ArrayList<>();

        rule.execute(loginContext, events::add);
        return events;
    }
}
