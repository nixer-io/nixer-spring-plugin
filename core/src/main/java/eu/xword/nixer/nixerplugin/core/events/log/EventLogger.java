package eu.xword.nixer.nixerplugin.core.events.log;

import eu.xword.nixer.nixerplugin.core.events.AnomalyEvent;
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
        // TODO consider logging event as JSON
        // TODO control logging level with config
        if (logger.isInfoEnabled()) {
            logger.info(event);
        }
    }
}
