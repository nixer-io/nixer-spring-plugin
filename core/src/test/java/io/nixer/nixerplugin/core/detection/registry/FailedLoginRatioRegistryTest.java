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

    private static final Instant NOW = Instant.ofEpochSecond(600);

    @Mock
    private NowSource nowSource;

    private FailedLoginRatioRegistry registry;


    @BeforeEach
    void setUp() {
        registry = new FailedLoginRatioRegistry(nowSource);
    }

    @Test
    void onDeactivationEvent() {
        when(nowSource.now()).thenReturn(NOW);

        registry.onApplicationEvent(new FailedLoginRatioDeactivationEvent(50));

        assertThat(registry.isFailedLoginRatioActivated()).isFalse();
    }

    @Test
    void onActivationEvent() {
        when(nowSource.now()).thenReturn(NOW);

        registry.onApplicationEvent(new FailedLoginRatioActivationEvent(70));

        assertThat(registry.isFailedLoginRatioActivated()).isTrue();
    }

    @Test
    void activationTimeout() {
        when(nowSource.now()).thenReturn(NOW);
        registry.onApplicationEvent(new FailedLoginRatioActivationEvent(70));

        // for ACTIVATION_TIMEOUT period from event registration, activation state is expected
        when(nowSource.now()).thenReturn(NOW.plus(Duration.ofMinutes(5)));
        assertThat(registry.isFailedLoginRatioActivated()).isTrue();

        // when ACTIVATION_TIMEOUT passes, deactivation state is expected
        when(nowSource.now()).thenReturn(NOW.plus(FailedLoginRatioRegistry.DEACTIVATION_TIMEOUT_ON_IDLE).plusSeconds(1));
        assertThat(registry.isFailedLoginRatioActivated()).isFalse();


    }
}
