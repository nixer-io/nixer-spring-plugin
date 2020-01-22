package io.nixer.nixerplugin.core.detection.registry;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import io.nixer.nixerplugin.core.detection.events.GlobalCredentialStuffingEvent;
import io.nixer.nixerplugin.core.util.NowSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CredentialStuffingRegistryTest {

    private static final Instant CS_EVENT_OCCURRENCE_TIME = LocalDateTime.of(2019, 5, 20, 13, 50, 15).toInstant(ZoneOffset.UTC);
    private static final Instant CS_EVENT_HANDLING_TIME = CS_EVENT_OCCURRENCE_TIME.plus(1, SECONDS);

    private static final Duration CS_DURATION = Duration.ofMinutes(15);

    @Spy
    private GlobalCredentialStuffingEvent credentialStuffingEvent = new GlobalCredentialStuffingEvent();

    @Mock
    private NowSource nowSource;

    private CredentialStuffingRegistry registry;

    @BeforeEach
    void setUp() {
        doReturn(CS_EVENT_OCCURRENCE_TIME.toEpochMilli()).when(credentialStuffingEvent).getTimestamp();

        given(nowSource.currentTimeMillis()).willReturn(CS_EVENT_HANDLING_TIME.toEpochMilli());

        registry = new CredentialStuffingRegistry(CS_DURATION, nowSource);
    }

    @Test
    void shouldReturnActiveOnEvent() {
        final long beforeCredentialStuffingExpires = CS_EVENT_OCCURRENCE_TIME.plus(CS_DURATION).minus(1, SECONDS).toEpochMilli();

        handleCSEvent();
        final boolean isActive = registry.hasHappenDuringCredentialStuffing(beforeCredentialStuffingExpires);

        assertTrue(isActive);
    }

    @Test
    void credentialStuffingShouldExpire() {
        final long afterCredentialStuffingExpired = CS_EVENT_OCCURRENCE_TIME.plus(CS_DURATION).plus(1, SECONDS).toEpochMilli();

        handleCSEvent();
        final boolean isActive = registry.hasHappenDuringCredentialStuffing(afterCredentialStuffingExpired);

        assertFalse(isActive);
    }

    private void handleCSEvent() {
        registry.onApplicationEvent(credentialStuffingEvent);
    }
}
