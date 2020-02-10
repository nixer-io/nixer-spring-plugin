package io.nixer.nixerplugin.core.detection.registry;

import java.time.Duration;
import java.time.Instant;

import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioActivationEvent;
import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioDeactivationEvent;
import io.nixer.nixerplugin.core.util.NowSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailedLoginRatioRegistryTest {

    @Mock
    private NowSource nowSource;

    private FailedLoginRatioRegistry registry;

    @Test
    void onDeactivationEvent() {
        registry = new FailedLoginRatioRegistry(new NowSource());
        registry.onApplicationEvent(new FailedLoginRatioDeactivationEvent(50));

        assertThat(registry.isFailedLoginRatioActivated()).isFalse();
    }

    @Test
    void onActivationEvent() {
        registry = new FailedLoginRatioRegistry(new NowSource());
        registry.onApplicationEvent(new FailedLoginRatioActivationEvent(70));

        assertThat(registry.isFailedLoginRatioActivated()).isTrue();
    }

    @Test
    void activationTimeout() {
        registry = new FailedLoginRatioRegistry(nowSource);
        when(nowSource.now()).thenReturn(Instant.now());

        registry.onApplicationEvent(new FailedLoginRatioActivationEvent(70));

        when(nowSource.now()).thenReturn(Instant.now().plus(Duration.ofMinutes(20)).plusSeconds(1));

        assertThat(registry.isFailedLoginRatioActivated()).isFalse();
    }
}
