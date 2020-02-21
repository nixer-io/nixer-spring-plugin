package io.nixer.nixerplugin.captcha.config;

import io.nixer.nixerplugin.captcha.security.CaptchaAuthenticationProvider;
import io.nixer.nixerplugin.captcha.security.CaptchaChecker;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.util.Assert;

/**
 * This class is used to integrate {@link CaptchaChecker} into Spring Authentication flow as a post processor.
 * Example usage:
 * <pre>{@code
 *         authenticationManagerBuilder.
 *            .jdbcAuthentication()
 *            .withObjectPostProcessor(new CaptchaConfigurer(...));
 * }</pre>
 * Currently this solution is not used in favor of {@link CaptchaAuthenticationProvider}.
 */
public class CaptchaConfigurer implements ObjectPostProcessor<DaoAuthenticationProvider> {

    private final CaptchaChecker captchaChecker;

    public CaptchaConfigurer(final CaptchaChecker captchaChecker) {
        Assert.notNull(captchaChecker, "CaptchaChecker must not be null");
        this.captchaChecker = captchaChecker;
    }

    @Override
    public DaoAuthenticationProvider postProcess(final DaoAuthenticationProvider object) {
        object.setPreAuthenticationChecks(captchaChecker);
        return object;
    }
}
