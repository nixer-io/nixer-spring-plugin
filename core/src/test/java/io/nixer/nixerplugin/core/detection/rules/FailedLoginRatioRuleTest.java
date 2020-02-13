package io.nixer.nixerplugin.core.detection.rules;

import java.util.ArrayList;
import java.util.List;

import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioActivationEvent;
import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioDeactivationEvent;
import io.nixer.nixerplugin.core.detection.rules.ratio.FailedLoginRatioRule;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;
import io.nixer.nixerplugin.core.login.inmemory.LoginMetric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailedLoginRatioRuleTest {

    @Mock
    private LoginMetric loginMetric;

    @Mock
    private LoginContext loginContext;

    @Test
    void doNothingWhenBelowMinimumSampleSize() {
        when(loginMetric.value(LoginResult.Status.SUCCESS.getName())).thenReturn(3);
        when(loginMetric.value(LoginResult.Status.FAILURE.getName())).thenReturn(3);
        int activationLevel = 50;
        int deactivationLevel = 50;
        int minimumSampleSize = 7;
        List<Object> events = new ArrayList<>();

        FailedLoginRatioRule rule = new FailedLoginRatioRule(loginMetric, activationLevel, deactivationLevel, minimumSampleSize);

        rule.execute(loginContext, events::add);

        assertThat(events).isEmpty();
    }

    @Test
    void doNothingWhenNoLoginEvents() {
        when(loginMetric.value(LoginResult.Status.SUCCESS.getName())).thenReturn(0);
        when(loginMetric.value(LoginResult.Status.FAILURE.getName())).thenReturn(0);
        int activationLevel = 50;
        int deactivationLevel = 50;
        int minimumSampleSize = 0;
        List<Object> events = new ArrayList<>();

        FailedLoginRatioRule rule = new FailedLoginRatioRule(loginMetric, activationLevel, deactivationLevel, minimumSampleSize);

        rule.execute(loginContext, events::add);

        assertThat(events).isEmpty();
    }

    @Test
    void deactivateWhenZeroFailureEvents() {
        when(loginMetric.value(LoginResult.Status.SUCCESS.getName())).thenReturn(6);
        when(loginMetric.value(LoginResult.Status.FAILURE.getName())).thenReturn(0);
        int activationLevel = 50;
        int deactivationLevel = 50;
        int minimumSampleSize = 6;
        List<Object> events = new ArrayList<>();

        FailedLoginRatioRule rule = new FailedLoginRatioRule(loginMetric, activationLevel, deactivationLevel, minimumSampleSize);

        rule.execute(loginContext, events::add);

        assertThat(events)
                .hasSize(1)
                .hasOnlyElementsOfType(FailedLoginRatioDeactivationEvent.class);
    }

    @Test
    void activateWhenZeroSuccessEvents() {
        when(loginMetric.value(LoginResult.Status.SUCCESS.getName())).thenReturn(0);
        when(loginMetric.value(LoginResult.Status.FAILURE.getName())).thenReturn(6);
        int activationLevel = 50;
        int deactivationLevel = 50;
        int minimumSampleSize = 6;
        List<Object> events = new ArrayList<>();

        FailedLoginRatioRule rule = new FailedLoginRatioRule(loginMetric, activationLevel, deactivationLevel, minimumSampleSize);

        rule.execute(loginContext, events::add);

        assertThat(events)
                .hasSize(1)
                .hasOnlyElementsOfType(FailedLoginRatioActivationEvent.class);
        double ratio = ((FailedLoginRatioActivationEvent) events.get(0)).getRatio();
        assertEquals(1, ratio, 0.0001);
    }

    @Test
    void activateWhenRatio() {
        when(loginMetric.value(LoginResult.Status.SUCCESS.getName())).thenReturn(4);
        when(loginMetric.value(LoginResult.Status.FAILURE.getName())).thenReturn(6);
        int activationLevel = 60;
        int deactivationLevel = 41;
        int minimumSampleSize = 10;
        List<Object> events = new ArrayList<>();

        FailedLoginRatioRule rule = new FailedLoginRatioRule(loginMetric, activationLevel, deactivationLevel, minimumSampleSize);

        rule.execute(loginContext, events::add);

        assertThat(events)
                .hasSize(1)
                .hasOnlyElementsOfType(FailedLoginRatioActivationEvent.class);
        double ratio = ((FailedLoginRatioActivationEvent) events.get(0)).getRatio();
        assertEquals(0.6, ratio, 0.0001);
    }

    @Test
    void deactivateWhenRatio() {
        when(loginMetric.value(LoginResult.Status.SUCCESS.getName())).thenReturn(6);
        when(loginMetric.value(LoginResult.Status.FAILURE.getName())).thenReturn(4);
        int activationLevel = 60;
        int deactivationLevel = 41;
        int minimumSampleSize = 10;
        List<Object> events = new ArrayList<>();

        FailedLoginRatioRule rule = new FailedLoginRatioRule(loginMetric, activationLevel, deactivationLevel, minimumSampleSize);

        rule.execute(loginContext, events::add);

        assertThat(events)
                .hasSize(1)
                .hasOnlyElementsOfType(FailedLoginRatioDeactivationEvent.class);
        double ratio = ((FailedLoginRatioDeactivationEvent) events.get(0)).getRatio();
        assertEquals(0.4, ratio, 0.0001);
    }
}
