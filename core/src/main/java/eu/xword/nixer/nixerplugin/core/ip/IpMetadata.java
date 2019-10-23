package eu.xword.nixer.nixerplugin.core.ip;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Represents additional data about ip address.
 * Such as if it was blacklisted, and possibly if that's proxy ip or country code
 */
public class IpMetadata {

    private boolean blacklisted;

    public IpMetadata(final boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final IpMetadata that = (IpMetadata) o;
        return blacklisted == that.blacklisted;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(blacklisted);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("blacklisted", blacklisted)
                .toString();
    }
}
