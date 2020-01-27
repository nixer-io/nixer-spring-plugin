package io.nixer.nixerplugin.stigma.token.validation;

import java.text.ParseException;
import javax.annotation.Nonnull;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.token.StigmaTokenConstants;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static java.lang.String.format;

/**
 * Verifies if the passed JWT is parsable and it's payload contains {@link Stigma}.
 *
 * Created on 2019-05-29.
 *
 * @author gcwiak
 */
public class StigmaTokenPayloadValidator {

    @Nonnull
    public ValidationResult validate(@Nonnull final JWT jwt) {
        Assert.notNull(jwt, "JWT must not be null");

        final JWTClaimsSet result;
        try {
            result = jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            return ValidationResult.invalid(ValidationStatus.PAYLOAD_PARSING_ERROR, format("Payload parsing error: [%s]", e.getMessage()));
        }

        return validatePayload(result);
    }

    private ValidationResult validatePayload(final JWTClaimsSet claims) {

        // TODO consider accumulating violations instead failing fast at first encountered one

        final Object stigmaValue = claims.getClaim(StigmaTokenConstants.STIGMA_VALUE_FIELD_NAME);
        if (stigmaValue == null || StringUtils.isEmpty(stigmaValue.toString())) {
            return ValidationResult.invalid(ValidationStatus.MISSING_STIGMA, "Missing stigma value");
        }

        final Stigma stigma = new Stigma(stigmaValue.toString());

        if (!StigmaTokenConstants.SUBJECT.equals(claims.getSubject())) {
            return ValidationResult.invalid(ValidationStatus.INVALID_PAYLOAD, format("Invalid subject: [%s]", claims.getSubject()), stigma);
        }

        return ValidationResult.valid(stigma);
    }
}
