package io.nixer.nixerplugin.core.stigma.storage;

import java.util.Objects;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;

/**
 * Created on 2019-06-05.
 *
 * @author gcwiak
 */
@Immutable
public class StigmaData {

    @Nonnull
    private final Stigma stigma;

    @Nonnull
    private final StigmaStatus status;

    public StigmaData(@Nonnull final Stigma stigma,
                      @Nonnull final StigmaStatus status) {
        this.stigma = Preconditions.checkNotNull(stigma, "stigma");
        this.status = Preconditions.checkNotNull(status, "status");
    }

    @Nonnull
    public Stigma getStigma() {
        return stigma;
    }

    @Nonnull
    public StigmaStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StigmaData that = (StigmaData) o;
        return stigma.equals(that.stigma) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stigma, status);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StigmaData.class.getSimpleName() + "[", "]")
                .add("stigma='" + stigma + "'")
                .add("status=" + status)
                .toString();
    }
}
