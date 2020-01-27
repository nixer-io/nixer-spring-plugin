package io.nixer.nixerplugin.stigma.token.validation;

import java.util.Objects;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import io.nixer.nixerplugin.stigma.domain.Stigma;
import org.springframework.util.Assert;

/**
 * Created on 2019-06-03.
 *
 * @author gcwiak
 */
@Immutable
public class ValidationResult {

    @Nonnull
    private final ValidationStatus status;

    @Nonnull
    private final String details;

    @Nullable
    private final Stigma stigma;

    @Nonnull
    public static ValidationResult invalid(@Nonnull final ValidationStatus status,
                                           @Nonnull final String details) {
        Assert.notNull(status, "status");
        Assert.state(!status.isValid(), () -> "Passed status " + status + " must not represent a valid one.");
        Assert.notNull(details, "details");

        return new ValidationResult(status, details, null);
    }

    @Nonnull
    public static ValidationResult invalid(@Nonnull final ValidationStatus status,
                                           @Nonnull final String details,
                                           @Nonnull final Stigma stigma) {
        Assert.notNull(status, "status");
        Assert.state(!status.isValid(), () -> "Passed status " + status + " must not represent a valid one.");
        Assert.notNull(details, "details");
        Assert.notNull(stigma, "stigma");

        return new ValidationResult(status, details, stigma);
    }

    @Nonnull
    public static ValidationResult valid(@Nonnull final Stigma stigma) {
        Assert.notNull(stigma, "stigma");
        return new ValidationResult(ValidationStatus.VALID, ValidationStatus.VALID.toString(), stigma);
    }

    private ValidationResult(@Nonnull final ValidationStatus status, @Nonnull final String details, @Nullable final Stigma stigma) {
        this.status = status;
        this.details = details;
        this.stigma = stigma;
    }

    @Nonnull
    public ValidationStatus getStatus() {
        return status;
    }

    @Nonnull
    public String getDetails() {
        return details;
    }

    @Nullable
    public Stigma getStigma() {
        return stigma;
    }

    public boolean isValid() {
        return this.status.isValid();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ValidationResult that = (ValidationResult) o;
        return status == that.status &&
                details.equals(that.details) &&
                Objects.equals(stigma, that.stigma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, details, stigma);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ValidationResult.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .add("details='" + details + "'")
                .add("stigma='" + stigma + "'")
                .toString();
    }
}