package io.nixer.nixerplugin.core.detection.events.log;

import io.nixer.nixerplugin.core.detection.events.AnomalyEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

/**
 * Writes {@link AnomalyEvent}s to logs.
 */
public class EventLogger implements ApplicationListener<AnomalyEvent> {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void onApplicationEvent(final AnomalyEvent event) {
        if (logger.isInfoEnabled()) {
            logger.info(event);
        }
    }
}
