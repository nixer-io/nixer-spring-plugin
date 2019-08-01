package eu.xword.nixer.nixerplugin.blocking.policies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ObserveOnlyMitigationStrategy implements MitigationStrategy {
    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response) {
        if (logger.isInfoEnabled()) {
            logger.info("Would block request " + request);
        }
    }
}
