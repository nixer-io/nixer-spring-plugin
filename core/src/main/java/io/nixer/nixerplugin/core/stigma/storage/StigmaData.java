package io.nixer.nixerplugin.core.stigma.storage;

import java.beans.ConstructorProperties;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

/**
 * Created on 2019-06-05.
 *
 * @author gcwiak
 */
@Immutable
public class StigmaData {

    @Nonnull
    private final UUID guid;

    @Nonnull
    private final String stigmaValue;

    @Nonnull
    private final StigmaStatus status;

    @ConstructorProperties({
            "guid",
            "stigma_value",
            "status",
    })
    public StigmaData(@Nonnull final UUID guid,
                      @Nonnull final String stigmaValue,
                      @Nonnull final StigmaStatus status) {
        this.guid = Preconditions.checkNotNull(guid, "guid");
        this.stigmaValue = Preconditions.checkNotNull(stigmaValue, "stigmaValue");
        this.status = Preconditions.checkNotNull(status, "status");
    }

    @Nonnull
    public UUID getGuid() {
        return guid;
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
        return guid.equals(that.guid) &&
                stigmaValue.equals(that.stigmaValue) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, stigmaValue, status);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StigmaData.class.getSimpleName() + "[", "]")
                .add("guid=" + guid)
                .add("stigmaValue='" + stigmaValue + "'")
                .add("status=" + status)
                .toString();
    }
}
