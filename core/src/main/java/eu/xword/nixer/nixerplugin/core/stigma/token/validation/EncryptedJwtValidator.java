package eu.xword.nixer.nixerplugin.core.stigma.token.validation;

import javax.annotation.Nonnull;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import eu.xword.nixer.nixerplugin.core.stigma.token.crypto.DecrypterFactory;
import org.springframework.util.Assert;

import static eu.xword.nixer.nixerplugin.core.stigma.token.validation.ValidationStatus.DECRYPTION_ERROR;
import static eu.xword.nixer.nixerplugin.core.stigma.token.validation.ValidationStatus.NOT_ENCRYPTED;
import static eu.xword.nixer.nixerplugin.core.stigma.token.validation.ValidationStatus.WRONG_ALG;
import static eu.xword.nixer.nixerplugin.core.stigma.token.validation.ValidationStatus.WRONG_ENC;
import static java.lang.String.format;

/**
 * Verifies if the passed JWT is encrypted with the expected algorithm and method, failing fast in case any mismatch.
 * After encryption parameters are successfully verified the JWT is decrypted and passed to the {@link #delegate} validator
 * for further verification.
 *
 * Created on 2019-05-29.
 *
 * @author gcwiak
 */
public class EncryptedJwtValidator implements JwtValidator {

    @Nonnull
    private final DecrypterFactory decrypterFactory;

    @Nonnull
    private final JwtValidator delegate;

    public EncryptedJwtValidator(@Nonnull final DecrypterFactory decrypterFactory, @Nonnull final JwtValidator delegate) {
        Assert.notNull(decrypterFactory, "DecrypterFactory must not be null");
        this.decrypterFactory = decrypterFactory;

        Assert.notNull(delegate, "JwtValidator must not be null");
        this.delegate = delegate;
    }

    @Override
    public ValidationResult validate(@Nonnull final JWT jwt) {
        Assert.notNull(jwt, "JWT must not be null");

        if (!(jwt instanceof EncryptedJWT)) {
            return ValidationResult.invalid(NOT_ENCRYPTED, format("Expected EncryptedJWT, but got [%s]", jwt.getClass()));
        }

        final EncryptedJWT encryptedJWT = (EncryptedJWT) jwt;

        final JWEHeader header = encryptedJWT.getHeader();

        if (!decrypterFactory.getAlgorithm().equals(header.getAlgorithm())) {
            return ValidationResult.invalid(WRONG_ALG,
                    format("Invalid encryption algorithm. Expected [%s] but got [%s]",
                            decrypterFactory.getAlgorithm(), header.getAlgorithm())
            );
        }

        if (!decrypterFactory.getEncryptionMethod().equals(header.getEncryptionMethod())) {
            return ValidationResult.invalid(WRONG_ENC,
                    format("Invalid encryption method. Expected [%s] but got [%s]",
                            decrypterFactory.getEncryptionMethod(), header.getEncryptionMethod())
            );
        }

        // TODO validate header.getContentType();

        try {
            encryptedJWT.decrypt(decrypterFactory.decrypter(header));
        } catch (JOSEException e) {
            return ValidationResult.invalid(DECRYPTION_ERROR, format("Decryption error: [%s]", e.getMessage()));
        }

        return delegate.validate(encryptedJWT);
    }
}
