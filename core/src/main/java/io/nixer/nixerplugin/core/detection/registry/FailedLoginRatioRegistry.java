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

    static final Duration DEACTIVATION_TIMEOUT_ON_IDLE = Duration.ofMinutes(20);

    private final NowSource nowSource;
    private FailedLoginRatioState state;


    public FailedLoginRatioRegistry(final NowSource nowSource) {
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
            return activationTime.plus(DEACTIVATION_TIMEOUT_ON_IDLE).isAfter(nowSource.now());
        }
    }

    @Override
    public void onApplicationEvent(final FailedLoginRatioEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("FAILED_LOGIN_RATIO event was caught with ratio: " + event.getRatio());
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
