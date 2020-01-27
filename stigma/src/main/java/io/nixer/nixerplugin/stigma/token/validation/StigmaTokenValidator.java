package io.nixer.nixerplugin.stigma.token.validation;

import java.text.ParseException;
import javax.annotation.Nonnull;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import static java.lang.String.format;

/**
 * Created on 2019-05-06.
 *
 * @author gcwiak
 */
public class StigmaTokenValidator {

    private final Log logger = LogFactory.getLog(getClass());

    private EncryptedJwtValidator encryptedJwtValidator;

    public StigmaTokenValidator(@Nonnull final EncryptedJwtValidator encryptedJwtValidator) {
        Assert.notNull(encryptedJwtValidator, "encryptedJwtValidator must not be null");
        this.encryptedJwtValidator = encryptedJwtValidator;
    }

    @Nonnull
    public ValidationResult validate(@Nonnull final RawStigmaToken token) {
        Assert.notNull(token, "token must not be null");

        try {
            final ValidationResult result = validateToken(token.getValue());
            if (!result.isValid() && logger.isDebugEnabled()) {
                logger.debug("Invalid token: " + result);
            }
            return result;

        } catch (Exception e) {
            logger.error("Unexpected validation error: " + e, e);
            return ValidationResult.invalid(ValidationStatus.UNEXPECTED_VALIDATION_ERROR, format("Unexpected validation error: %s", e));
        }
    }

    private ValidationResult validateToken(@Nonnull final String token) {

        final JWT jwt;

        try {
            jwt = JWTParser.parse(token);
        } catch (ParseException e) {
            return ValidationResult.invalid(ValidationStatus.PARSING_ERROR, format("Details: [%s]", e.getMessage()));
        }

        return encryptedJwtValidator.validate(jwt);
    }
}
