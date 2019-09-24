package eu.xword.nixer.nixerplugin.captcha.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Checks that a given string is valid captcha response.
 */
@Component
public class CaptchaValidator implements ConstraintValidator<Captcha, String> {

    private CaptchaService captchaService;

    @Autowired
    private CaptchaServiceFactory captchaServiceFactory;

    @Override
    public void initialize(final Captcha annotation) {
        this.captchaService = captchaServiceFactory.createCaptchaService(annotation.action());
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        try {
            captchaService.processResponse(value);
            return true;
        } catch (RecaptchaException e) {
            return false;
        }
    }
}
