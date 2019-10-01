package eu.xword.nixer.nixerplugin.captcha.security;

import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.config.RecaptchaProperties;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaException;
import eu.xword.nixer.nixerplugin.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.login.LoginFailures;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

/**
 * Integrates captcha verification capability in spring-security authentication process.
 */
public class CaptchaChecker implements UserDetailsChecker, InitializingBean {

    @Autowired
    private HttpServletRequest request;

    private String captchaParam = RecaptchaProperties.DEFAULT_CAPTCHA_PARAM;

    private CaptchaService captchaService;

    // TODO consider removing condition and controlling it with request param + default actions
    private AtomicReference<CaptchaCondition> condition = new AtomicReference<>(CaptchaCondition.AUTOMATIC);

    private LoginFailures loginFailures;

    public CaptchaChecker(final CaptchaService captchaService,
                          final LoginFailures loginFailures) {
        Assert.notNull(captchaService, "CaptchaService must not be null");
        this.captchaService = captchaService;

        Assert.notNull(loginFailures, "LoginFailure must not be null");
        this.loginFailures = loginFailures;
    }

    @Override
    public void afterPropertiesSet() {
        loginFailures.addMapping(BadCaptchaException.class, LoginFailureType.INVALID_CAPTCHA);
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

    public void setRequest(final HttpServletRequest request) {
        Assert.notNull(captchaParam, "CaptchaParam must not be null");

        this.request = request;
    }

    public void setCaptchaParam(final String captchaParam) {
        Assert.notNull(captchaParam, "CaptchaParam must not be null");

        this.captchaParam = captchaParam;
    }
}
