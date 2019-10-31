package eu.xword.nixer.nixerplugin.core.detection.registry;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import eu.xword.nixer.nixerplugin.core.detection.events.GlobalCredentialStuffingEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CredentialStuffingRegistryTest {

    private final CredentialStuffingRegistry registry = new CredentialStuffingRegistry();

    @Test
    void shouldReturnActiveOnEvent() {
        registry.onApplicationEvent(new GlobalCredentialStuffingEvent());

        final boolean isActive = registry.hasHappenDuringCredentialStuffing(now());

        assertTrue(isActive);
    }

    @Test
    void credentialStuffingShouldExpire() {
        registry.onApplicationEvent(new GlobalCredentialStuffingEvent());

        final boolean isActive = registry.hasHappenDuringCredentialStuffing(future());

        assertFalse(isActive);
    }

    private long future() {
        return Instant.now()
                .plus(1, ChronoUnit.DAYS)
                .toEpochMilli();
    }

    private long now() {
        return Instant.now().toEpochMilli();
    }


}