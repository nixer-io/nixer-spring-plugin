package eu.xword.nixer.nixerplugin.filter.strategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogBehavior implements MitigationStrategy {
    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) {
        if (logger.isInfoEnabled()) {
            logger.info("Would block request " + request);
        }
    }
}
