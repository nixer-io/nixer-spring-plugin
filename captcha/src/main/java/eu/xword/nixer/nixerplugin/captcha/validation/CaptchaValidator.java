package eu.xword.nixer.nixerplugin.captcha.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.error.CaptchaException;
import org.springframework.util.Assert;

/**
 * Checks that a given string is valid captcha response.
 */
public class CaptchaValidator implements ConstraintValidator<Captcha, String> {

    private CaptchaService captchaService;

    private final CaptchaServiceFactory captchaServiceFactory;

    public CaptchaValidator(final CaptchaServiceFactory captchaServiceFactory) {
        Assert.notNull(captchaServiceFactory, "CaptchaServiceFactory must not be null");
        this.captchaServiceFactory = captchaServiceFactory;
    }

    @Override
    public void initialize(final Captcha annotation) {
        this.captchaService = captchaServiceFactory.createCaptchaService(annotation.action());
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        try {
            captchaService.verifyResponse(value);
            return true;
        } catch (CaptchaException e) {
            return false;
        }
    }
}