package io.nixer.nixerplugin.captcha.security;

import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.captcha.CaptchaService;
import io.nixer.nixerplugin.captcha.config.CaptchaConfigurer;
import io.nixer.nixerplugin.captcha.error.CaptchaException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

/**
 * This class offers methods for captcha verification for both:
 * post-processor ({@link CaptchaChecker}) and authentication provider ({@link CaptchaAuthenticationProvider}) integration methods.
 */
public class CaptchaChecker implements UserDetailsChecker, InitializingBean {

    private HttpServletRequest request;

    private final String captchaParam;

    private final CaptchaService captchaService;

    private final AtomicReference<CaptchaCondition> condition = new AtomicReference<>(CaptchaCondition.ALWAYS);

    public CaptchaChecker(final CaptchaService captchaService, final String captchaParam) {
        Assert.notNull(captchaService, "CaptchaService must not be null");
        this.captchaService = captchaService;

        Assert.notNull(captchaParam, "CaptchaParam must not be null");
        this.captchaParam = captchaParam;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(request, "HttpServletRequest must not be null");
    }

    /**
     * This method will be called when Captcha verification is integrated as a post processor.
     * This is not the default option.
     * See {@link CaptchaConfigurer}.
     */
    @Override
    public void check(final UserDetails toCheck) {
        try {
            checkCaptcha();
        } catch (CaptchaException e) {
            throw new BadCaptchaException("Invalid captcha", e);
        }
    }

    /**
     * This method is explicitly called in captcha authentication provider.
     * See {@link CaptchaAuthenticationProvider}
     */
    public void checkCaptcha() {
        if (shouldVerifyCaptcha()) {
            final String captchaValue = getCaptchaParameter();

            captchaService.verifyResponse(captchaValue);
        }
    }

    private String getCaptchaParameter() {
        return request.getParameter(captchaParam);
    }

    private boolean shouldVerifyCaptcha() {
        return condition.get().test(request);
    }

    /**
     * Whether captcha should be displayed. To be used in view to control display of captcha.
     */
    public boolean shouldDisplayCaptcha() {
        return condition.get().test(request);
    }

    public void setCaptchaCondition(final CaptchaCondition captchaCondition) {
        Assert.notNull(captchaCondition, "CaptchaCondition must not be null");

        this.condition.set(captchaCondition);
    }

    public CaptchaCondition getCaptchaCondition() {
        return condition.get();
    }

    /**
     * Spring is injecting proxy to actual request
     */
    @Autowired
    public void setRequest(final HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest must not be null");

        this.request = request;
    }
}
