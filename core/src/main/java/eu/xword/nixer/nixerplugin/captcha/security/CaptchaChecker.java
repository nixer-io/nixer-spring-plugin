package eu.xword.nixer.nixerplugin.captcha.security;

import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.config.CaptchaLoginProperties;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaException;
import eu.xword.nixer.nixerplugin.captcha.strategy.CaptchaStrategies;
import eu.xword.nixer.nixerplugin.captcha.strategy.CaptchaStrategy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

// TODO convert to Blocking policy

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

    private AtomicReference<CaptchaStrategy> captchaStrategy = new AtomicReference<>(CaptchaStrategies.ALWAYS);

    public CaptchaChecker(final CaptchaServiceFactory captchaServiceFactory, final CaptchaLoginProperties captchaLoginProperties) {
        Assert.notNull(captchaServiceFactory, "CaptchaServiceFactory must not be null");
        this.captchaServiceFactory = captchaServiceFactory;

        Assert.notNull(captchaLoginProperties, "RecaptchaProperties must not be null");
        this.captchaParam = captchaLoginProperties.getParam();
    }

    @Override
    public void afterPropertiesSet() {
        if (captchaService == null) {
            this.captchaService = captchaServiceFactory.createCaptchaService(LOGIN_ACTION);
        }
    }

    @Override
    public void check(final UserDetails toCheck) {
        if (shouldVerifyCaptcha()) {
            final String captchaValue = request.getParameter(captchaParam);

            try {
                captchaService.processResponse(captchaValue);
            } catch (RecaptchaException e) {
                throw new BadCaptchaException("Invalid captcha", e);
            }
        }
    }


    private boolean shouldVerifyCaptcha() {
        return captchaStrategy.get().verifyChallenge();
    }
    // Needs to be public for template engine to check if captcha should be displayed.
    // TODO Re-implement check so it doesn't have to be public

    public boolean shouldDisplayCaptcha() {
        return captchaStrategy.get().challenge();
    }

    public void setCaptchaStrategy(final CaptchaStrategy captchaStrategy) {
        Assert.notNull(captchaStrategy, "CaptchaStrategy must not be null");

        this.captchaStrategy.set(captchaStrategy);
    }

    public CaptchaStrategy getCaptchaStrategy() {
        return captchaStrategy.get();
    }

    public void setCaptchaService(final CaptchaService captchaService) {
        Assert.notNull(captchaService, "CaptchaService must not be null");

        this.captchaService = captchaService;
    }
}
