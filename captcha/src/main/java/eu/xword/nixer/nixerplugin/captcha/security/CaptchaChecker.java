package eu.xword.nixer.nixerplugin.captcha.security;

import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.config.LoginCaptchaProperties;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

/**
 * Integrates captcha verification capability in spring-security authentication process.
 */
public class CaptchaChecker implements UserDetailsChecker, InitializingBean {

    private static final String LOGIN_ACTION = "login";

    @Autowired
    private HttpServletRequest request;

    private CaptchaServiceFactory captchaServiceFactory;

    private String captchaParam;

    private CaptchaService captchaService;

    private AtomicReference<CaptchaCondition> condition = new AtomicReference<>(CaptchaCondition.AUTOMATIC);

    public CaptchaChecker(final CaptchaServiceFactory captchaServiceFactory, final LoginCaptchaProperties loginCaptchaProperties) {
        Assert.notNull(captchaServiceFactory, "CaptchaServiceFactory must not be null");
        this.captchaServiceFactory = captchaServiceFactory;

        Assert.notNull(loginCaptchaProperties, "RecaptchaProperties must not be null");
        this.captchaParam = loginCaptchaProperties.getParam();
    }

    @Override
    public void afterPropertiesSet() {
        if (captchaService == null) {
            this.captchaService = captchaServiceFactory.createCaptchaService(LOGIN_ACTION);
        }
    }

    @Override
    public void check(final UserDetails toCheck) {
        if (condition.get().test(request)) {
            final String captchaValue = request.getParameter(captchaParam);

            try {
                captchaService.processResponse(captchaValue);
            } catch (RecaptchaException e) {
                throw new BadCaptchaException("Invalid captcha", e);
            }
        }
    }

    //    private boolean shouldVerifyCaptcha() {
//        return captchaStrategy.get().verifyChallenge();
//    }
//    // Needs to be public for template engine to check if captcha should be displayed.
//    // TODO Re-implement check so it doesn't have to be public
//
    public boolean shouldDisplayCaptcha() {
//        return captchaStrategy.get().challenge();
        return condition.get().test(request);
    }

    //
    public void setCaptchaCondition(final CaptchaCondition captchaCondition) {
        Assert.notNull(captchaCondition, "CaptchaCondition must not be null");

        this.condition.set(captchaCondition);
    }

    public CaptchaCondition getCaptchaCondition() {
        return condition.get();
    }

    public void setCaptchaService(final CaptchaService captchaService) {
        Assert.notNull(captchaService, "CaptchaService must not be null");

        this.captchaService = captchaService;
    }
}
