package io.nixer.nixerplugin.core.detection.rules.threshold;

import java.util.ArrayList;
import java.util.List;

import io.nixer.nixerplugin.core.detection.events.FingerprintFailedLoginOverThresholdEvent;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.inmemory.LoginMetric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FingerprintFailedLoginOverThresholdRuleTest {

    private static final int UNDER_THRESHOLD = FingerprintFailedLoginOverThresholdRule.THRESHOLD_VALUE - 1;
    private static final int OVER_THRESHOLD = FingerprintFailedLoginOverThresholdRule.THRESHOLD_VALUE + 1;

    private static final String FINGERPRINT = "fingerprint";

    @Mock
    private LoginContext loginContext;

    @Mock
    private LoginMetric failedLoginMetric;

    @InjectMocks
    private FingerprintFailedLoginOverThresholdRule rule;

    private final List<Object> emittedEvents = new ArrayList<>();

    @Test
    void should_emit_event_when_failed_login_threshold_exceeded() {
        // given
        given(loginContext.getFingerprint()).willReturn(FINGERPRINT);
        given(failedLoginMetric.value(FINGERPRINT)).willReturn(OVER_THRESHOLD);

        // when
        rule.execute(loginContext, emittedEvents::add);

        // then
        assertThat(emittedEvents).containsExactly(new FingerprintFailedLoginOverThresholdEvent(FINGERPRINT));
    }

    @ParameterizedTest
    @ValueSource(ints = {
            UNDER_THRESHOLD,
            FingerprintFailedLoginOverThresholdRule.THRESHOLD_VALUE
    })
    void should_not_emit_event_when_failed_login_threshold_not_exceeded(int notOverThresholdValue) {
        // given
        given(loginContext.getFingerprint()).willReturn(FINGERPRINT);
        given(failedLoginMetric.value(FINGERPRINT)).willReturn(notOverThresholdValue);

        // when
        rule.execute(loginContext, emittedEvents::add);

        // then
        assertThat(emittedEvents).isEmpty();
    }
}