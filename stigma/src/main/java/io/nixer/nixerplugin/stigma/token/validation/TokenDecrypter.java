package io.nixer.nixerplugin.stigma.token.validation;

import javax.annotation.Nonnull;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import io.nixer.nixerplugin.stigma.crypto.DecrypterFactory;
import org.springframework.util.Assert;

import static io.nixer.nixerplugin.stigma.token.validation.DecryptedToken.DecryptionStatus.DECRYPTION_ERROR;
import static io.nixer.nixerplugin.stigma.token.validation.DecryptedToken.DecryptionStatus.NOT_ENCRYPTED;
import static io.nixer.nixerplugin.stigma.token.validation.DecryptedToken.DecryptionStatus.WRONG_ALG;
import static io.nixer.nixerplugin.stigma.token.validation.DecryptedToken.DecryptionStatus.WRONG_ENC;
import static java.lang.String.format;

/**
 * Decrypts stigma tokens passed as encrypted JWT, that is JWE.
 *
 * Created on 2019-05-29.
 *
 * @author gcwiak
 */
public class TokenDecrypter {

    @Nonnull
    private final DecrypterFactory decrypterFactory;

    public TokenDecrypter(@Nonnull final DecrypterFactory decrypterFactory) {
        Assert.notNull(decrypterFactory, "decrypterFactory must not be null");
        this.decrypterFactory = decrypterFactory;
    }

    /**
     * Checks if the passed JWT is encrypted with the expected algorithm and method, failing fast in case any mismatch.
     * After encryption parameters are successfully verified the JWT is decrypted and wrapped with {@link DecryptedToken}.
     * Otherwise returns {@link DecryptedToken} representing invalid result with failure details.
     *
     * @param jwt token to be decrypted
     * @return decryption result, valid or invalid, never null
     */
    @Nonnull
    DecryptedToken decrypt(@Nonnull final JWT jwt) {
        Assert.notNull(jwt, "JWT must not be null");

        if (!(jwt instanceof EncryptedJWT)) {
            return DecryptedToken.invalid(NOT_ENCRYPTED, format("Expected EncryptedJWT, but got [%s]", jwt.getClass()));
        }

        final EncryptedJWT encryptedJWT = (EncryptedJWT) jwt;

        final JWEHeader header = encryptedJWT.getHeader();

        if (!decrypterFactory.getAlgorithm().equals(header.getAlgorithm())) {
            return DecryptedToken.invalid(WRONG_ALG,
                    format("Invalid encryption algorithm. Expected [%s] but got [%s]",
                            decrypterFactory.getAlgorithm(), header.getAlgorithm())
            );
        }

        if (!decrypterFactory.getEncryptionMethod().equals(header.getEncryptionMethod())) {
            return DecryptedToken.invalid(WRONG_ENC,
                    format("Invalid encryption method. Expected [%s] but got [%s]",
                            decrypterFactory.getEncryptionMethod(), header.getEncryptionMethod())
            );
        }

        try {
            encryptedJWT.decrypt(decrypterFactory.decrypter(header));
        } catch (JOSEException e) {
            return DecryptedToken.invalid(DECRYPTION_ERROR, format("Decryption error: [%s]", e.getMessage()));
        }

        return DecryptedToken.valid(encryptedJWT);
    }
}
