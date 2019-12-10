package io.nixer.nixerplugin.core.stigma.evaluate;

import java.util.Objects;
import java.util.StringJoiner;
import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.core.stigma.domain.RawStigmaToken;

/**
 * Represents a raw serialized stigma token and information if the previously used token was valid or not.
 *
 * Created on 2019-06-25.
 *
 * @author gcwiak
 */
public class StigmaTokenFetchResult {

    @Nonnull
    private final RawStigmaToken fetchedToken;

    private final boolean originalTokenValid;

    public StigmaTokenFetchResult(@Nonnull final RawStigmaToken fetchedToken, final boolean originalTokenValid) {
        this.fetchedToken = Preconditions.checkNotNull(fetchedToken, "fetchedToken");
        this.originalTokenValid = originalTokenValid;
    }

    @Nonnull
    public RawStigmaToken getFetchedToken() {
        return fetchedToken;
    }

    public boolean isOriginalTokenValid() {
        return originalTokenValid;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StigmaTokenFetchResult that = (StigmaTokenFetchResult) o;
        return originalTokenValid == that.originalTokenValid &&
                fetchedToken.equals(that.fetchedToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fetchedToken, originalTokenValid);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StigmaTokenFetchResult.class.getSimpleName() + "[", "]")
                .add("fetchedToken='" + fetchedToken + "'")
                .add("originalTokenValid=" + originalTokenValid)
                .toString();
    }
}
