package io.nixer.nixerplugin.captcha;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.nixer.nixerplugin.core.detection.filter.behavior.Behavior;
import io.nixer.nixerplugin.core.detection.filter.behavior.Behavior;
import io.nixer.nixerplugin.core.detection.filter.behavior.Behaviors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static io.nixer.nixerplugin.core.detection.filter.behavior.Behaviors.CAPTCHA;

/**
 * Challenges user with captcha. Sets flag {@link #CAPTCHA_CHALLENGE_SESSION_ATTR} in session based on which captcha is displayed and then verified
 */
public class CaptchaBehavior implements Behavior {

    /**
     * Defines session attribute name that will be used to control captcha challenge.
     */
    public static final String CAPTCHA_CHALLENGE_SESSION_ATTR = "nixer.session.captcha.challenge";

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final HttpSession session = request.getSession();
        session.setAttribute(CAPTCHA_CHALLENGE_SESSION_ATTR, true);
    }

    @Override
    public boolean isCommitting() {
        return false;
    }

    @Override
    public String name() {
        return Behaviors.CAPTCHA.name();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
