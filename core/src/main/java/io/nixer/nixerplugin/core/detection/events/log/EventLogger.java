package io.nixer.nixerplugin.core.detection.events.log;

import io.nixer.nixerplugin.core.detection.events.AnomalyEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

/**
 * Writes {@link AnomalyEvent}s to logs.
 */
public class EventLogger implements ApplicationListener<AnomalyEvent> {

    private static final Log logger = LogFactory.getLog(EventLogger.class);

    @Override
    public void onApplicationEvent(final AnomalyEvent event) {
        // TODO consider logging event as JSON
        // TODO control logging level with config
        if (logger.isInfoEnabled()) {
            logger.info(event);
        }
    }
}
