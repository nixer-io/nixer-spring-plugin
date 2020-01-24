package io.nixer.nixerplugin.stigma.token;

import javax.annotation.Nonnull;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import io.nixer.nixerplugin.stigma.crypto.EncrypterFactory;
import io.nixer.nixerplugin.stigma.domain.Stigma;
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
public class EncryptedStigmaTokenProvider {

    private final Log logger = LogFactory.getLog(getClass());

    private final EncrypterFactory encrypterFactory;

    public EncryptedStigmaTokenProvider(final EncrypterFactory encrypterFactory) {
        this.encrypterFactory = encrypterFactory;
    }

    @Nonnull
    public JWT getToken(@Nonnull final Stigma stigma) {
        Assert.notNull(stigma, "stigma must not be null");

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
                .subject(StigmaTokenConstants.SUBJECT)
                .claim(StigmaTokenConstants.STIGMA_VALUE_FIELD_NAME, stigma.getValue())
                .build();
    }
}
