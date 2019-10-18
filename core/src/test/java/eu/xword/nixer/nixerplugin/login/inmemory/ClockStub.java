package eu.xword.nixer.nixerplugin.login.inmemory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * This class allows to control time. It points to given instant that could be controlled by calling {@link #tick(Duration)}.
 */
public class ClockStub extends Clock {

    private Instant instant;
    private ZoneId zoneId;

    public ClockStub() {
        this(Instant.now());
    }

    public ClockStub(final Instant instant) {
        this.instant = instant;
        this.zoneId = ZoneId.systemDefault();
    }

    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    @Override
    public Clock withZone(final ZoneId zone) {
        this.zoneId = zone;
        return this;
    }

    @Override
    public Instant instant() {
        return instant;
    }

    /**
     * Advances time by given duration.
     */
    public ClockStub tick(Duration duration) {
        instant = instant.plus(duration);
        return this;
    }
}
