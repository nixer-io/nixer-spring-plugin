package io.nixer.nixerplugin.stigma.token.validation;

import java.text.ParseException;
import javax.annotation.Nonnull;

import com.nimbusds.jwt.JWTClaimsSet;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.token.StigmaTokenConstants;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static io.nixer.nixerplugin.stigma.token.validation.ParsedToken.ParsingStatus.INVALID_PAYLOAD;
import static io.nixer.nixerplugin.stigma.token.validation.ParsedToken.ParsingStatus.MISSING_STIGMA;
import static io.nixer.nixerplugin.stigma.token.validation.ParsedToken.ParsingStatus.PAYLOAD_PARSING_ERROR;
import static java.lang.String.format;

/**
 * Parses stigma tokens from JWT to representation providing {@link Stigma}.
 *
 * Created on 2019-05-29.
 *
 * @author gcwiak
 */
public class TokenParser {

    /**
     * Checks if the passed JWT contains valid payload and parses it to {@link ParsedToken}.
     * If payload is not valid returns {@link ParsedToken} representing invalid result with failure details.
     *
     * @param decryptedToken to be parsed
     * @return parsing result, valid or invalid, never null
     */
    @Nonnull
    ParsedToken parse(@Nonnull final DecryptedToken decryptedToken) {
        Assert.notNull(decryptedToken, "decryptedToken must not be null");
        Assert.state(decryptedToken.isValid(), () -> "Expected valid decryptedToken, but got: " + decryptedToken);

        final JWTClaimsSet result;
        try {
            result = decryptedToken.getPayload();
        } catch (ParseException e) {
            return ParsedToken.invalid(PAYLOAD_PARSING_ERROR, format("Payload parsing error: [%s]", e.getMessage()));
        }

        return parsePayload(result);
    }

    private ParsedToken parsePayload(final JWTClaimsSet claims) {

        final Object stigmaValue = claims.getClaim(StigmaTokenConstants.STIGMA_VALUE_FIELD_NAME);
        if (stigmaValue == null || StringUtils.isEmpty(stigmaValue.toString())) {
            return ParsedToken.invalid(MISSING_STIGMA, format("Missing stigma. Claims were: [%s]", claims));
        }

        final Stigma stigma = new Stigma(stigmaValue.toString());

        if (!StigmaTokenConstants.SUBJECT.equals(claims.getSubject())) {
            return ParsedToken.invalid(INVALID_PAYLOAD, format("Invalid subject. Claims were: [%s]", claims));
        }

        return ParsedToken.valid(stigma);
    }
}
