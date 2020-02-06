package io.nixer.nixerplugin.core.detection.registry;

import java.time.Duration;
import java.time.Instant;

import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioEvent;
import io.nixer.nixerplugin.core.util.NowSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

public class FailedLoginRatioRegistry implements ApplicationListener<FailedLoginRatioEvent> {
    private final Log logger = LogFactory.getLog(getClass());

    private final NowSource nowSource;
    private final Duration deactivationPeriodOnIdle = Duration.ofMinutes(20);
    private Instant failedLoginRatioActivation = Instant.MIN;
    private boolean active;


    public FailedLoginRatioRegistry(final NowSource nowSource) {
        this.nowSource = nowSource;
    }

    public boolean isFailedLoginRatioActivated() {
        if (!active) {
            return false;
        } else {
            return failedLoginRatioActivation.plus(deactivationPeriodOnIdle).isAfter(nowSource.now());
        }
    }

    @Override
    public void onApplicationEvent(final FailedLoginRatioEvent event) {
        logger.debug("FAILED_LOGIN_RATIO event was caught with ratio: " + event.getSource());

        if (FailedLoginRatioEvent.FAILED_LOGIN_RATIO_ACTIVATION.equals(event.type())) {
            failedLoginRatioActivation = nowSource.now();
            active = true;
        } else if(FailedLoginRatioEvent.FAILED_LOGIN_RATIO_DEACTIVATION.equals(event.type())) {
            active = false;
        }

    }
}
