package io.nixer.nixerplugin.stigma.token.reading;

import java.text.ParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Created on 19/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaExtractor {

    private final Log logger = LogFactory.getLog(getClass());

    private final TokenDecrypter tokenDecrypter;

    private final TokenParser tokenParser;

    public StigmaExtractor(final TokenDecrypter tokenDecrypter, final TokenParser tokenParser) {
        this.tokenDecrypter = tokenDecrypter;
        this.tokenParser = tokenParser;
    }

    @Nullable
    public Stigma extractStigma(@Nonnull final RawStigmaToken stigmaToken) {
        Assert.notNull(stigmaToken, "stigmaToken must not be null");

        final JWT jwt;
        try {
            jwt = JWTParser.parse(stigmaToken.getValue());
        } catch (ParseException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Parsing stigma token to JWT failed.", e);
            }
            return null;
        }

        final DecryptedToken decryptedToken = tokenDecrypter.decrypt(jwt);
        if (!decryptedToken.isValid()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to decrypt token: " + decryptedToken);
            }
            return null;
        }

        final ParsedToken parsedToken = tokenParser.parse(decryptedToken);
        if (!parsedToken.isValid()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse token: " + parsedToken);
            }
            return null;
        }

        return parsedToken.getStigma();
    }
}
