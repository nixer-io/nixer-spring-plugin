package io.nixer.nixerplugin.core.stigma.token.validation;

import java.util.Objects;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

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
    private final String stigmaValue;

    @Nonnull
    public static ValidationResult invalid(@Nonnull final ValidationStatus status,
                                           @Nonnull final String details) {
        Assert.notNull(status, "status");
        Assert.state(!status.isValid(), () -> "Passed status " + status + " must not represent a valid one.");
        Assert.state(!status.isReadable(), () -> "Passed status " + status + " must represent one with readable stigma.");
        Assert.notNull(details, "details");

        return new ValidationResult(status, details, null);
    }

    @Nonnull
    public static ValidationResult invalid(@Nonnull final ValidationStatus status,
                                           @Nonnull final String details,
                                           @Nonnull final String stigmaValue) {
        Assert.notNull(status, "status");
        Assert.state(!status.isValid(), () -> "Passed status " + status + " must not represent a valid one.");
        Assert.state(status.isReadable(), () -> "Passed status " + status + " must represent one with readable stigma.");
        Assert.notNull(details, "details");
        Assert.notNull(stigmaValue, "stigmaValue");

        return new ValidationResult(status, details, stigmaValue);
    }

    @Nonnull
    public static ValidationResult valid(@Nonnull final String stigmaValue) {
        Assert.notNull(stigmaValue, "stigmaValue");
        return new ValidationResult(ValidationStatus.VALID, ValidationStatus.VALID.toString(), stigmaValue);
    }

    private ValidationResult(@Nonnull final ValidationStatus status, @Nonnull final String details, @Nullable final String stigmaValue) {
        this.status = status;
        this.details = details;
        this.stigmaValue = stigmaValue;
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
    public String getStigmaValue() {
        return stigmaValue;
    }

    public boolean isValid() {
        return this.status.isValid();
    }

    public boolean isReadable() {
        return status.isReadable();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ValidationResult that = (ValidationResult) o;
        return status == that.status &&
                details.equals(that.details) &&
                Objects.equals(stigmaValue, that.stigmaValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, details, stigmaValue);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ValidationResult.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .add("details='" + details + "'")
                .add("stigmaValue='" + stigmaValue + "'")
                .toString();
    }
}
