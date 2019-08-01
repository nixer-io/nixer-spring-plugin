package eu.xword.nixer.nixerplugin.blocking.policies;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.blocking.events.ActivateCaptchaEvent;
import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.RecaptchaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

@Component
public class CaptchaChecker implements UserDetailsChecker, ApplicationListener<ActivateCaptchaEvent> {

    // TODO convert to Blocking policy
    private static final String LOGIN_ACTION = "login";

    @Autowired
    private HttpServletRequest request;

    private CaptchaService captchaService;

    @Autowired
    private CaptchaServiceFactory captchaServiceFactory;

    @Autowired
    private RecaptchaProperties recaptchaProperties;

    private final AtomicBoolean captchaEnabled = new AtomicBoolean(true);

    @PostConstruct
    public void postInit() {
        this.captchaService = captchaServiceFactory.createCaptchaService(LOGIN_ACTION);
    }

    @Override
    public void check(final UserDetails toCheck) {
        // TODO consider replacing with event listener that way CS will be publish as event and interested bean will be listening
        if (isCaptchaEnabled()) {
            final String captchaValue = request.getParameter(recaptchaProperties.getParam());

            try {
                captchaService.processResponse(captchaValue);
            } catch (Exception e) {
                // TODO consider creating custom BadCaptchaException and custom event BadCaptchaException
                throw new BadCaptchaException("Invalid captcha", e);
            }
        }
    }

    public boolean isCaptchaEnabled() {
        return captchaEnabled.get();
    }

    @Override
    public void onApplicationEvent(final ActivateCaptchaEvent event) {
        //TODO schedule unlock after sometime/expect DropCaptcha event
        captchaEnabled.set(true);
    }
}
