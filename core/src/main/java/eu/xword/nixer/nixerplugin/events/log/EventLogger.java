package eu.xword.nixer.nixerplugin.events.log;

import eu.xword.nixer.nixerplugin.events.DetectionEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

/**
 * Writes {@link DetectionEvent}s to logs.
 */
public class EventLogger implements ApplicationListener<DetectionEvent> {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void onApplicationEvent(final DetectionEvent event) {
        // TODO consider logging event as JSON
        // TODO control logging level with config
        if (logger.isInfoEnabled()) {
            logger.info(event);
        }
    }
}
