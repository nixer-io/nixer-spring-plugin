package io.nixer.nixerplugin.captcha.security;

import io.nixer.nixerplugin.captcha.error.CaptchaException;
import io.nixer.nixerplugin.captcha.events.FailedCaptchaAuthenticationEvent;
import io.nixer.nixerplugin.captcha.security.BadCaptchaException;
import io.nixer.nixerplugin.captcha.security.CaptchaAuthenticationStatusException;
import io.nixer.nixerplugin.captcha.security.CaptchaChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

public class CaptchaAuthenticationProvider implements AuthenticationProvider {

    private CaptchaChecker captchaChecker;

    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setCaptchaChecker(final CaptchaChecker captchaChecker) {
        Assert.notNull(captchaChecker, "CaptchaChecker must not be null");
        this.captchaChecker = captchaChecker;
    }

    @Autowired
    void setEventPublisher(final ApplicationEventPublisher eventPublisher) {
        Assert.notNull(captchaChecker, "ApplicationEventPublisher must not be null");
        this.eventPublisher = eventPublisher;
    }

    /**
     * This method has no authority to authenticate a request, therefore it should never return {@link Authentication} object.
     * It should return {@code null} when captcha is correct or does not need to be checked.
     * It should throw an {@link AccountStatusException} when captcha is incorrect.
     *
     * See {@link ProviderManager} for authentication loop details.
     *
     * @param authentication
     * @return null when captcha is correct
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        try {
            captchaChecker.checkCaptcha();
        } catch (CaptchaException e) {
            authentication.setAuthenticated(false);

            final FailedCaptchaAuthenticationEvent event = new FailedCaptchaAuthenticationEvent(
                    authentication,
                    new BadCaptchaException("invalid captcha", e));
            eventPublisher.publishEvent(event);

            throw new CaptchaAuthenticationStatusException("invalid captcha");
        }
        return null;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
