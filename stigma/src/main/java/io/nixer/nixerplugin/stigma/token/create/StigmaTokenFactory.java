package io.nixer.nixerplugin.stigma.token.create;

import javax.annotation.Nonnull;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import io.nixer.nixerplugin.stigma.crypto.EncrypterFactory;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.StigmaConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Creates Stigma Tokens as encrypted <a href="https://en.wikipedia.org/wiki/JSON_Web_Token">JWT</a>,
 * that is <a href="https://en.wikipedia.org/wiki/JSON_Web_Encryption">JWE</a>.
 * <br>
 * Uses encryption provided by the given {@link EncrypterFactory}.
 *
 * Created on 2019-05-20.
 *
 * @author gcwiak
 */
public class StigmaTokenFactory {

    private final Log logger = LogFactory.getLog(getClass());

    private final EncrypterFactory encrypterFactory;

    public StigmaTokenFactory(final EncrypterFactory encrypterFactory) {
        this.encrypterFactory = encrypterFactory;
    }

    /**
     * Creates stigma token as JWT serialized to String and wrapped with {@link RawStigmaToken}.
     *
     * @param stigma value of stigma to be put inside stigma token, into JWT claims.
     * @return serialized stigma token.
     */
    @Nonnull
    public RawStigmaToken getToken(@Nonnull final Stigma stigma) {
        Assert.notNull(stigma, "stigma must not be null");

        final JWT jwt = getJWT(stigma);

        return new RawStigmaToken(jwt.serialize());
    }

    private JWT getJWT(final Stigma stigma) {
        final JWTClaimsSet claimsSet = buildClaims(stigma);

        final JWEHeader header =
                new JWEHeader.Builder(encrypterFactory.getAlgorithm(), encrypterFactory.getEncryptionMethod())
                        .keyID(encrypterFactory.getKeyId()).build();

        final EncryptedJWT jwe = new EncryptedJWT(header, claimsSet);

        try {
            jwe.encrypt(encrypterFactory.encrypter());
        } catch (JOSEException e) {
            logger.error("Unable to encrypt token, keyID=" + encrypterFactory.getKeyId(), e);
            throw new RuntimeException(e);
        }

        return jwe;
    }

    private JWTClaimsSet buildClaims(final Stigma stigma) {
        return new JWTClaimsSet.Builder()
                .subject(StigmaConstants.SUBJECT)
                .claim(StigmaConstants.STIGMA_VALUE_FIELD_NAME, stigma.getValue())
                .build();
    }
}
