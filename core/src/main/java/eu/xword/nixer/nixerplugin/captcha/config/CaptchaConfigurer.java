package eu.xword.nixer.nixerplugin.captcha.config;

import eu.xword.nixer.nixerplugin.captcha.security.CaptchaChecker;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;

public class CaptchaConfigurer implements ObjectPostProcessor<DaoAuthenticationProvider> {

    private CaptchaChecker captchaChecker;

    public CaptchaConfigurer(final CaptchaChecker captchaChecker) {
        this.captchaChecker = captchaChecker;
    }

    @Override
    public DaoAuthenticationProvider postProcess(final DaoAuthenticationProvider object) {
        object.setPreAuthenticationChecks(captchaChecker);
        return object;
    }
}
