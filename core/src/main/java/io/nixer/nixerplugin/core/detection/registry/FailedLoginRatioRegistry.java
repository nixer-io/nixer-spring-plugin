package io.nixer.nixerplugin.core.detection.registry;

import java.security.Timestamp;

import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

public class FailedLoginRatioRegistry implements ApplicationListener<FailedLoginRatioEvent> {

    private final Log logger = LogFactory.getLog(getClass());

    private boolean active;
    private double ratio;
    private long timestamp;

    public boolean isFailedLoginRatioActive() {
        return active;
    }

    public double getFailedLoginRatio() {
        return ratio;
    }

    @Override
    public void onApplicationEvent(final FailedLoginRatioEvent event) {
        logger.debug("FAILED_LOGIN_RATIO event was caught with ratio: " + event.getSource());

        if (isFailedLoginRatioActive()) {
            if (FailedLoginRatioEvent.FAILED_LOGIN_RATIO_ACTIVATION.equals(event.type())) {
                logger.info("FAILED_LOGIN_RATIO_ACTIVATION was caught when registry state was already in active state.");
            }
            this.active = true;
            this.ratio = (double) event.getSource();
            this.timestamp = event.getTimestamp();

        } else {
            if (FailedLoginRatioEvent.FAILED_LOGIN_RATIO_DEACTIVATION.equals(event.type())) {
                logger.info("FAILED_LOGIN_RATIO_DEACTIVATION was caught when registry state was already in deactive state.");
            }
            this.active = false;
            this.ratio = (double) event.getSource();
            this.timestamp = event.getTimestamp();
        }


    }
}
