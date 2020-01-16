package io.nixer.nixerplugin.stigma.token.validation;

import java.text.ParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static java.lang.String.format;

/**
 * Created on 2019-05-06.
 *
 * @author gcwiak
 */
public class StigmaTokenValidator {

    private final Log logger = LogFactory.getLog(getClass());

    private static final ValidationResult MISSING_TOKEN_RESULT = ValidationResult.invalid(ValidationStatus.MISSING, "Missing token");

    private JwtValidator jwtValidator;

    public StigmaTokenValidator(@Nonnull final JwtValidator jwtValidator) {
        Assert.notNull(jwtValidator, "JwtValidator must not be null");

        this.jwtValidator = jwtValidator;
    }

    @Nonnull
    public ValidationResult validate(@Nullable final RawStigmaToken token) {

        if (missing(token)) {
            logger.trace("Missing token");
            return MISSING_TOKEN_RESULT;
        }

        try {
            final ValidationResult result = validatePresentToken(token.getValue());
            if (!result.isValid() && logger.isDebugEnabled()) {
                logger.debug("Invalid token: " + result);
            }
            return result;

        } catch (Exception e) {
            logger.error("Unexpected validation error: " + e, e);
            return ValidationResult.invalid(ValidationStatus.UNEXPECTED_VALIDATION_ERROR, format("Unexpected validation error: %s", e));
        }
    }

    private boolean missing(final RawStigmaToken token) {
        return token == null || !StringUtils.hasText(token.getValue());
    }

    private ValidationResult validatePresentToken(@Nonnull final String token) {

        final JWT jwt;

        try {
            jwt = JWTParser.parse(token);
        } catch (ParseException e) {
            return ValidationResult.invalid(ValidationStatus.PARSING_ERROR, format("Details: [%s]", e.getMessage()));
        }

        return jwtValidator.validate(jwt);
    }
}
