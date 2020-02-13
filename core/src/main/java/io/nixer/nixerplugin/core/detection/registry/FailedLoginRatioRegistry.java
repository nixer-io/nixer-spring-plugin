package io.nixer.nixerplugin.core.detection.registry;

import java.time.Duration;
import java.time.Instant;

import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioEvent;
import io.nixer.nixerplugin.core.util.NowSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

public class FailedLoginRatioRegistry implements ApplicationListener<FailedLoginRatioEvent> {
    private static final Log logger = LogFactory.getLog(FailedLoginRatioRegistry.class);

    private final NowSource nowSource;
    private final Duration deactivationPeriodOnIdle;
    private FailedLoginRatioState state;


    public FailedLoginRatioRegistry(final Duration deactivationPeriodOnIdle, final NowSource nowSource) {
        this.deactivationPeriodOnIdle = deactivationPeriodOnIdle;
        this.nowSource = nowSource;

        this.state = new FailedLoginRatioState(false, Instant.MIN);
    }

    public boolean isFailedLoginRatioActivated() {
        final FailedLoginRatioState ref = state;
        final Instant activationTime = ref.getActivationTime();
        final boolean active = ref.isActive();

        if (!active) {
            return false;
        } else {
            return activationTime.plus(deactivationPeriodOnIdle).isAfter(nowSource.now());
        }
    }

    @Override
    public void onApplicationEvent(final FailedLoginRatioEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("FAILED_LOGIN_RATIO event was caught with ratio: " + event.getSource());
        }

        if (FailedLoginRatioEvent.FAILED_LOGIN_RATIO_ACTIVATION.equals(event.type())) {
            this.state = new FailedLoginRatioState(true, nowSource.now());
        } else if (FailedLoginRatioEvent.FAILED_LOGIN_RATIO_DEACTIVATION.equals(event.type())) {
            this.state = new FailedLoginRatioState(false, nowSource.now());
        } else {
            throw new IllegalArgumentException("FAILED_LOGIN_RATIO event was caught with unknown type: " + event.type());
        }

    }

    private static class FailedLoginRatioState {
        private final boolean active;
        private final Instant activationTime;

        FailedLoginRatioState(final boolean active, final Instant activationTime) {
            this.active = active;
            this.activationTime = activationTime;
        }

        boolean isActive() {
            return active;
        }

        Instant getActivationTime() {
            return activationTime;
        }
    }

}
