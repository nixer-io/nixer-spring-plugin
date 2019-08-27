package eu.xword.nixer.nixerplugin.captcha.strategy;

import javax.servlet.http.HttpSession;

import eu.xword.nixer.nixerplugin.detection.GlobalCredentialStuffing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// TODO should we move it to eu.xword.nixer.nixerplugin.captcha.strategy
//TODO think how to rewrite. Currently it has to me spring bean to work

/**
 * Decides whether user should be challenged with captcha depending if Credential Stuffing is active.
 */
public class AutomaticCaptchaStrategy implements CaptchaStrategy {

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

    @Override
    public boolean challenge() {
        return globalCredentialStuffing.isCredentialStuffingActive();
    }

    @Override
    public boolean verifyChallenge() {
        final Long timestamp = sessionCreationTimeOrNull();

        return timestamp != null
                ? globalCredentialStuffing.hasHappenDuringCredentialStuffing(timestamp)
                : globalCredentialStuffing.isCredentialStuffingActive();
    }

    @Override
    public String name() {
        return "AUTOMATIC";
    }

}
