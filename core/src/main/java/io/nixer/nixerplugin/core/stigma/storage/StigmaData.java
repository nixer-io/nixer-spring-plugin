package io.nixer.nixerplugin.core.stigma.storage;

import java.util.Objects;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;

/**
 * Created on 2019-06-05.
 *
 * @author gcwiak
 */
@Immutable
public class StigmaData {

    @Nonnull
    private final String stigmaValue;

    @Nonnull
    private final StigmaStatus status;

    public StigmaData(@Nonnull final String stigmaValue,
                      @Nonnull final StigmaStatus status) {
        this.stigmaValue = Preconditions.checkNotNull(stigmaValue, "stigmaValue");
        this.status = Preconditions.checkNotNull(status, "status");
    }

    @Nonnull
    public String getStigmaValue() {
        return stigmaValue;
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
        return stigmaValue.equals(that.stigmaValue) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stigmaValue, status);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StigmaData.class.getSimpleName() + "[", "]")
                .add("stigmaValue='" + stigmaValue + "'")
                .add("status=" + status)
                .toString();
    }
}
