package io.nixer.nixerplugin.captcha.security;

import io.nixer.nixerplugin.captcha.error.CaptchaException;
import io.nixer.nixerplugin.captcha.events.FailedCaptchaAuthenticationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class CaptchaAuthenticationProvider implements AuthenticationProvider {

    private final CaptchaChecker captchaChecker;

    private final ApplicationEventPublisher eventPublisher;

    public CaptchaAuthenticationProvider(final CaptchaChecker captchaChecker, final ApplicationEventPublisher eventPublisher) {
        this.captchaChecker = captchaChecker;
        this.eventPublisher = eventPublisher;
    }

    /**
     * This method has no authority to authenticate a request, therefore it should never return {@link Authentication} object.
     * It should return {@code null} when captcha is correct or does not need to be checked.
     * It should throw an {@link AccountStatusException} when captcha is incorrect.
     * <p>
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
