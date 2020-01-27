package io.nixer.nixerplugin.stigma.token.validation;

import javax.annotation.Nonnull;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import io.nixer.nixerplugin.stigma.crypto.DecrypterFactory;
import org.springframework.util.Assert;

import static java.lang.String.format;

/**
 * Verifies if the passed JWT is encrypted with the expected algorithm and method, failing fast in case any mismatch.
 * After encryption parameters are successfully verified the JWT is decrypted and passed to the {@link #stigmaTokenPayloadValidator} validator
 * for further verification.
 *
 * Created on 2019-05-29.
 *
 * @author gcwiak
 */
public class EncryptedJwtValidator {

    @Nonnull
    private final DecrypterFactory decrypterFactory;

    @Nonnull
    private final StigmaTokenPayloadValidator stigmaTokenPayloadValidator;

    public EncryptedJwtValidator(@Nonnull final DecrypterFactory decrypterFactory,
                                 @Nonnull final StigmaTokenPayloadValidator stigmaTokenPayloadValidator) {
        Assert.notNull(stigmaTokenPayloadValidator, "stigmaTokenPayloadValidator must not be null");
        Assert.notNull(decrypterFactory, "decrypterFactory must not be null");
        this.decrypterFactory = decrypterFactory;
        this.stigmaTokenPayloadValidator = stigmaTokenPayloadValidator;
    }

    @Nonnull
    public ValidationResult validate(@Nonnull final JWT jwt) {
        Assert.notNull(jwt, "JWT must not be null");

        if (!(jwt instanceof EncryptedJWT)) {
            return ValidationResult.invalid(ValidationStatus.NOT_ENCRYPTED, format("Expected EncryptedJWT, but got [%s]", jwt.getClass()));
        }

        final EncryptedJWT encryptedJWT = (EncryptedJWT) jwt;

        final JWEHeader header = encryptedJWT.getHeader();

        if (!decrypterFactory.getAlgorithm().equals(header.getAlgorithm())) {
            return ValidationResult.invalid(ValidationStatus.WRONG_ALG,
                    format("Invalid encryption algorithm. Expected [%s] but got [%s]",
                            decrypterFactory.getAlgorithm(), header.getAlgorithm())
            );
        }

        if (!decrypterFactory.getEncryptionMethod().equals(header.getEncryptionMethod())) {
            return ValidationResult.invalid(ValidationStatus.WRONG_ENC,
                    format("Invalid encryption method. Expected [%s] but got [%s]",
                            decrypterFactory.getEncryptionMethod(), header.getEncryptionMethod())
            );
        }

        try {
            encryptedJWT.decrypt(decrypterFactory.decrypter(header));
        } catch (JOSEException e) {
            return ValidationResult.invalid(ValidationStatus.DECRYPTION_ERROR, format("Decryption error: [%s]", e.getMessage()));
        }

        return validatePayload(encryptedJWT);
    }

    private ValidationResult validatePayload(final EncryptedJWT encryptedJWT) {
        return stigmaTokenPayloadValidator.validate(encryptedJWT);
    }
}
