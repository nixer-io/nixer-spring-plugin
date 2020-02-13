package io.nixer.nixerplugin.core.detection.registry;

import java.time.Duration;
import java.time.Instant;

import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioActivationEvent;
import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioDeactivationEvent;
import io.nixer.nixerplugin.core.util.NowSource;
import org.junit.jupiter.api.BeforeEach;
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

    private final Instant baseInstant = Instant.ofEpochSecond(600);

    @BeforeEach
    void setUp() {
        registry = new FailedLoginRatioRegistry(Duration.ofMinutes(20), nowSource);
    }

    @Test
    void onDeactivationEvent() {
        when(nowSource.now()).thenReturn(baseInstant);

        registry.onApplicationEvent(new FailedLoginRatioDeactivationEvent(50));

        assertThat(registry.isFailedLoginRatioActivated()).isFalse();
    }

    @Test
    void onActivationEvent() {
        when(nowSource.now()).thenReturn(baseInstant);

        registry.onApplicationEvent(new FailedLoginRatioActivationEvent(70));

        assertThat(registry.isFailedLoginRatioActivated()).isTrue();
    }

    @Test
    void activationTimeout() {
        when(nowSource.now()).thenReturn(baseInstant,
                baseInstant.plus(Duration.ofMinutes(5)),
                baseInstant.plus(Duration.ofMinutes(20)).plusSeconds(1));

        registry.onApplicationEvent(new FailedLoginRatioActivationEvent(70));

        // when 20 minutes passed, activation should timeout
        assertThat(registry.isFailedLoginRatioActivated()).isTrue();
        assertThat(registry.isFailedLoginRatioActivated()).isFalse();
    }
}
