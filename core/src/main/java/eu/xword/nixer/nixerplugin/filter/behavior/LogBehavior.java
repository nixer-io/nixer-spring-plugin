package eu.xword.nixer.nixerplugin.filter.behavior;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogBehavior implements Behavior {
    private final Log logger = LogFactory.getLog(getClass());

    public static final String LOG = "log";

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) {
        if (logger.isInfoEnabled()) {
            logger.info("Request " + request);
        }
    }

    @Override
    public Category category() {
        return Category.STACKABLE;
    }

    @Override
    public String name() {
        return LOG;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
