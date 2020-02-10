package io.nixer.nixerplugin.stigma.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

/**
 * Created on 2019-06-05.
 *
 * @author gcwiak
 */
@Immutable
public class StigmaDetails {

    @Nonnull
    private final Stigma stigma;

    @Nonnull
    private final StigmaStatus status;

    @Nonnull
    private final Instant creationDate;

    public StigmaDetails(@Nonnull final Stigma stigma,
                         @Nonnull final StigmaStatus status,
                         @Nonnull final Instant creationDate) {
        this.stigma = Preconditions.checkNotNull(stigma, "stigma");
        this.status = Preconditions.checkNotNull(status, "status");
        this.creationDate = Preconditions.checkNotNull(creationDate, "creationDate");
    }

    @Nonnull
    public Stigma getStigma() {
        return stigma;
    }

    @Nonnull
    public StigmaStatus getStatus() {
        return status;
    }

    @Nonnull
    public Instant getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StigmaDetails that = (StigmaDetails) o;
        return stigma.equals(that.stigma) &&
                status == that.status &&
                creationDate.equals(that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stigma, status, creationDate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StigmaDetails.class.getSimpleName() + "[", "]")
                .add("stigma=" + stigma)
                .add("status=" + status)
                .add("creationDate=" + creationDate)
                .toString();
    }
}
