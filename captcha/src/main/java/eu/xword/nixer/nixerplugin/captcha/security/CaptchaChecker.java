package eu.xword.nixer.nixerplugin.captcha.security;

import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaException;
import eu.xword.nixer.nixerplugin.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.login.LoginFailureTypeRegistry;
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

    private String captchaParam;

    private CaptchaService captchaService;

    private AtomicReference<CaptchaCondition> condition = new AtomicReference<>(CaptchaCondition.ALWAYS);

    private LoginFailureTypeRegistry loginFailureTypeRegistry;

    public CaptchaChecker(final CaptchaService captchaService,
                          final LoginFailureTypeRegistry loginFailureTypeRegistry) {
        Assert.notNull(captchaService, "CaptchaService must not be null");
        this.captchaService = captchaService;

        Assert.notNull(loginFailureTypeRegistry, "LoginFailure must not be null");
        this.loginFailureTypeRegistry = loginFailureTypeRegistry;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(request, "HttpServletRequest must not be null");
        Assert.notNull(captchaParam, "CaptchaParam must not be null");

        //todo refactor
        loginFailureTypeRegistry.addMapping(BadCaptchaException.class, LoginFailureType.INVALID_CAPTCHA);
    }

    @Override
    public void check(final UserDetails toCheck) {
        if (shouldVerifyCaptcha()) {
            final String captchaValue = request.getParameter(captchaParam);

            try {
                captchaService.verifyResponse(captchaValue);
            } catch (CaptchaException e) {
                throw new BadCaptchaException("Invalid captcha", e);
            }
        }
    }

    private boolean shouldVerifyCaptcha() {
        return condition.get().test(request);
    }

    //    // Needs to be public for template engine to check if captcha should be displayed.
//    // TODO Re-implement check so it doesn't have to be public
//
    public boolean shouldDisplayCaptcha() {
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
        Assert.notNull(request, "HttpServletRequest must not be null");

        this.request = request;
    }

    public void setCaptchaParam(final String captchaParam) {
        Assert.notNull(captchaParam, "CaptchaParam must not be null");

        this.captchaParam = captchaParam;
    }
}
