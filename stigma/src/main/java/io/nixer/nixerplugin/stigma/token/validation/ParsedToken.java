package io.nixer.nixerplugin.stigma.token.validation;

import java.util.Objects;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.stigma.domain.Stigma;
import org.springframework.util.Assert;

import static io.nixer.nixerplugin.stigma.token.validation.ParsedToken.ParsingStatus.VALID;

/**
 * Created on 27/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
class ParsedToken {

    enum ParsingStatus {
        // One and only valid result
        VALID(true),

        // Invalid results
        PAYLOAD_PARSING_ERROR,
        MISSING_STIGMA,
        INVALID_PAYLOAD;

        final boolean isValid;

        ParsingStatus(final boolean isValid) {
            this.isValid = isValid;
        }

        ParsingStatus() {
            this(false);
        }
    }

    @Nonnull
    private final ParsingStatus status;

    @Nonnull
    private final String description;

    @Nullable
    private final Stigma stigma;

    @Nonnull
    static ParsedToken invalid(@Nonnull final ParsingStatus status,
                               @Nonnull final String description) {
        Assert.notNull(status, "status");
        Assert.state(!status.isValid, () -> "Passed status " + status + " must not represent a valid one.");
        Assert.notNull(description, "description");

        return new ParsedToken(status, description, null);
    }

    @Nonnull
    static ParsedToken valid(@Nonnull final Stigma stigma) {
        Assert.notNull(stigma, "stigma must not be null");

        return new ParsedToken(VALID, VALID.toString(), stigma);
    }

    private ParsedToken(@Nonnull final ParsingStatus status, @Nonnull final String description, @Nullable final Stigma stigma) {
        this.status = status;
        this.description = description;
        this.stigma = stigma;
    }

    boolean isValid() {
        return status.isValid;
    }

    @Nonnull
    ParsingStatus getStatus() {
        return status;
    }

    @Nonnull
    Stigma getStigma() {
        Assert.state(isValid() && stigma != null, () -> "Expecting valid state, but was: " + this);
        return stigma;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ParsedToken that = (ParsedToken) o;
        return status == that.status &&
                description.equals(that.description) &&
                Objects.equals(stigma, that.stigma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, description, stigma);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParsedToken.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .add("description='" + description + "'")
                .add("stigma=" + stigma)
                .toString();
    }
}
