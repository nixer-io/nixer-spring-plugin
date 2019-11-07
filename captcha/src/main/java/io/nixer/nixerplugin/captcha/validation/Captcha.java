package io.nixer.nixerplugin.captcha.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * The string has to be a valid captcha response. Exactly what captcha provider is used for verification is left for validator.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CaptchaValidator.class)
public @interface Captcha {
    String message() default "{io.nixer.nixerplugin.captcha.validation.Captcha.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String action() default "global";
}
