package io.nixer.nixerplugin.core.stigma.token;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import io.nixer.nixerplugin.core.stigma.crypto.EncrypterFactory;
import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Encrypts all tokens produced by the wrapped {@link StigmaTokenProvider} delegate into JWE objects.
 *
 * Uses encryption provided by the given {@link EncrypterFactory}.
 *
 * Created on 2019-05-20.
 *
 * @author gcwiak
 */
public class EncryptedStigmaTokenProvider implements StigmaTokenProvider {

    private final Log logger = LogFactory.getLog(getClass());

    private final StigmaTokenProvider delegate;

    private final EncrypterFactory encrypterFactory;

    public EncryptedStigmaTokenProvider(final StigmaTokenProvider delegate, final EncrypterFactory encrypterFactory) {
        this.delegate = delegate;
        this.encrypterFactory = encrypterFactory;
    }


    @Override
    public JWT getToken(final Stigma stigma) {

        final JWTClaimsSet claimsSet = getClaims(stigma);

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

    private JWTClaimsSet getClaims(final Stigma stigma) {
        try {
            return delegate.getToken(stigma).getJWTClaimsSet();
        } catch (ParseException e) {
            logger.error("Unable to extract claims from plain token", e);
            throw new RuntimeException(e);
        }
    }
}
