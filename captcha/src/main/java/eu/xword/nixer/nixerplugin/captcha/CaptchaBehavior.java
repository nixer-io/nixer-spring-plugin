package eu.xword.nixer.nixerplugin.captcha;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import eu.xword.nixer.nixerplugin.filter.behavior.Behavior;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Challenges user with captcha. Sets flag in session based on which captcha is displayed and then verified
 */
@Component
public class CaptchaBehavior implements Behavior {

    private final Log logger = LogFactory.getLog(getClass());

    public static final String CAPTCHA = "captcha";
    public static final String CAPTCHA_CHALLENGE = "nixer.captcha.challenge";

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(CAPTCHA_CHALLENGE, true);
        } else {
            logger.warn("Expecting session to be created by this time");
        }
    }

    @Override
    public Category category() {
        return Category.STACKABLE;
    }

    @Override
    public String name() {
        return CAPTCHA;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
