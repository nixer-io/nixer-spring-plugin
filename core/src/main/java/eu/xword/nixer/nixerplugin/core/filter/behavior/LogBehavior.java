package eu.xword.nixer.nixerplugin.core.filter.behavior;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static eu.xword.nixer.nixerplugin.core.filter.behavior.Behaviors.LOG;

/**
 * This behaviors logs request.
 */
public class LogBehavior implements Behavior {

    private final Log logger = LogFactory.getLog(getClass());

    //todo consider whether http tracking would be replacement or addition to it

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
        return LOG.name();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
