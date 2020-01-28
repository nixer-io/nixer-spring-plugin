package io.nixer.nixerplugin.stigma.token.validation;

import java.text.ParseException;
import java.util.Objects;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.util.Assert;

import static io.nixer.nixerplugin.stigma.token.validation.DecryptedToken.DecryptionStatus.VALID;

/**
 * Created on 27/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
class DecryptedToken {

    enum DecryptionStatus {
        // One and only valid result
        VALID(true),

        // Invalid results
        NOT_ENCRYPTED,
        WRONG_ALG,
        WRONG_ENC,
        DECRYPTION_ERROR;

        final boolean isValid;

        DecryptionStatus(final boolean isValid) {
            this.isValid = isValid;
        }

        DecryptionStatus() {
            this(false);
        }
    }

    @Nonnull
    private final DecryptionStatus status;

    @Nonnull
    private final String description;

    @Nullable
    private final EncryptedJWT decryptedToken;

    @Nonnull
    static DecryptedToken invalid(@Nonnull final DecryptionStatus status,
                                  @Nonnull final String description) {
        Assert.notNull(status, "status");
        Assert.state(!status.isValid, () -> "Passed status " + status + " must not represent a valid one.");
        Assert.notNull(description, "description");

        return new DecryptedToken(status, description, null);
    }

    @Nonnull
    static DecryptedToken valid(@Nonnull final EncryptedJWT decryptedToken) {
        Assert.notNull(decryptedToken, "decryptedToken must not be null");
        Assert.state(decryptedToken.getState() == JWEObject.State.DECRYPTED,
                () -> String.format("Expecting %s state, but got %s", JWEObject.State.DECRYPTED, decryptedToken.getState()));

        return new DecryptedToken(VALID, VALID.toString(), decryptedToken);
    }

    private DecryptedToken(@Nonnull final DecryptionStatus status,
                           @Nonnull final String description,
                           @Nullable final EncryptedJWT decryptedToken) {
        this.status = status;
        this.description = description;
        this.decryptedToken = decryptedToken;
    }

    boolean isValid() {
        return status.isValid;
    }

    @Nonnull
    JWTClaimsSet getPayload() throws ParseException {
        Assert.state(isValid() && decryptedToken != null, () -> "Expecting valid state, but was: " + this);
        return decryptedToken.getJWTClaimsSet();
    }

    @Nonnull
    DecryptionStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DecryptedToken that = (DecryptedToken) o;
        return status == that.status &&
                description.equals(that.description) &&
                Objects.equals(decryptedToken, that.decryptedToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, description, decryptedToken);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DecryptedToken.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .add("description='" + description + "'")
                .add("decryptedToken=" + decryptedToken)
                .toString();
    }
}
