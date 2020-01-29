package io.nixer.nixerplugin.stigma.decision;

import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;

/**
 * Represents decision whether a Stigma should be refreshed or not.
 * If the refresh is required, by means of the {@link #event}, the carried {@link #tokenForRefresh} is to be used.
 * <br>
 * The {@link #tokenForRefresh} is considered <b>always valid and ready to be used for refreshing</b>, despite the decision.
 * It might be a brand new token or a valid, already being used, one.
 * <br>
 *
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
@Immutable
public class StigmaRefreshDecision {

    @Nonnull
    private final RawStigmaToken tokenForRefresh;

    @Nonnull
    private final StigmaEvent event;

    public StigmaRefreshDecision(@Nonnull final RawStigmaToken tokenForRefresh, @Nonnull final StigmaEvent event) {
        this.tokenForRefresh = Preconditions.checkNotNull(tokenForRefresh, "tokenForRefresh");
        this.event = Preconditions.checkNotNull(event, "event");
    }

    public boolean requiresStigmaRefresh() {
        return event.requiresStigmaRefresh;
    }

    @Nonnull
    public RawStigmaToken getTokenForRefresh() {
        return tokenForRefresh;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StigmaRefreshDecision that = (StigmaRefreshDecision) o;

        if (!tokenForRefresh.equals(that.tokenForRefresh)) return false;
        return event == that.event;

    }

    @Override
    public int hashCode() {
        int result = tokenForRefresh.hashCode();
        result = 31 * result + event.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StigmaRefreshDecision.class.getSimpleName() + "[", "]")
                .add("tokenForRefresh='" + tokenForRefresh + "'")
                .add("event=" + event)
                .toString();
    }
}
