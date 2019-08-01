package eu.xword.nixer.nixerplugin.stigma.token.validation;

import javax.annotation.Nonnull;

import com.nimbusds.jwt.JWT;

/**
 * Created on 2019-05-29.
 *
 * @author gcwiak
 */
public interface JwtValidator {

    ValidationResult validate(@Nonnull JWT jwt);
}
