package eu.xword.nixer.nixerplugin.action.strategy;

import javax.servlet.http.HttpSession;

import eu.xword.nixer.nixerplugin.detection.GlobalCredentialStuffing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//TODO think how to rewrite. Currently it has to me spring bean to work

/**
 * Decides whether user should be challenged with captcha depending if Credential Stuffing is active.
 */
public class AutomaticCaptchaStrategy {

    private GlobalCredentialStuffing globalCredentialStuffing;

    public AutomaticCaptchaStrategy(final GlobalCredentialStuffing globalCredentialStuffing) {
        this.globalCredentialStuffing = globalCredentialStuffing;
    }

    private Long sessionCreationTimeOrNull() {
        //TODO extract
        ServletRequestAttributes attr = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);

        return session != null
                ? session.getCreationTime()
                : null;
    }

    public boolean challenge() {
        return globalCredentialStuffing.isCredentialStuffingActive();
    }

    public boolean verifyChallenge() {
        final Long timestamp = sessionCreationTimeOrNull();

        return timestamp != null
                ? globalCredentialStuffing.hasHappenDuringCredentialStuffing(timestamp)
                : globalCredentialStuffing.isCredentialStuffingActive();
    }

    public String name() {
        return "AUTOMATIC";
    }

}
