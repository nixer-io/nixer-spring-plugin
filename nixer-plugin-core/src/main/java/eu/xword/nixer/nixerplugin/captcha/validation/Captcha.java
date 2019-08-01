package eu.xword.nixer.nixerplugin.captcha.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;

import com.nimbusds.jose.Payload;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CaptchaValidator.class)
public @interface Captcha {
    String message() default "{eu.xword.nixer.nixerplugin.captcha.validation.Captcha.message}"; // TODO parameterize

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String action();
}
