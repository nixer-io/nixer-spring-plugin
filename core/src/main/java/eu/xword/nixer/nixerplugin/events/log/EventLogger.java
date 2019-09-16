package eu.xword.nixer.nixerplugin.events.log;

import eu.xword.nixer.nixerplugin.events.BlockEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

/**
 * Writes {@link BlockEvent}s to logs.
 */
public class EventLogger implements ApplicationListener<BlockEvent> {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void onApplicationEvent(final BlockEvent event) {
        // TODO consider logging event as JSON
        // TODO control logging level with config
        if (logger.isInfoEnabled()) {
            logger.info(event);
        }
    }
}
