package eu.xword.nixer.nixerplugin.blocking;

import eu.xword.nixer.nixerplugin.blocking.events.BlockEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AuditingBlockEventsListener implements ApplicationListener<BlockEvent> {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void onApplicationEvent(final BlockEvent event) {
        if (logger.isInfoEnabled()) {
            logger.info(event);
        }
    }
}
