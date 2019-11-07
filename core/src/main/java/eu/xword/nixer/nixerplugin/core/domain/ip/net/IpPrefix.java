package eu.xword.nixer.nixerplugin.core.domain.ip.net;

import com.google.common.base.Objects;
import com.google.common.net.InetAddresses;
import org.springframework.util.Assert;

/**
 * Represents CIDR block.
 */
public class IpPrefix<T extends IpAddress> {

    private static final int MAX_IPV4_MASK = 32;
    private static final int MAX_IPV6_MASK = 128;

    private T address;
    private int mask;

    private IpPrefix(final T address, final int mask) {
        this.address = address;
        this.mask = mask;
    }

    public T getAddress() {
        return address;
    }

    public int getMask() {
        return mask;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final IpPrefix ipPrefix = (IpPrefix) o;
        return address == ipPrefix.address &&
                mask == ipPrefix.mask;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(address, mask);
    }

    @Override
    public String toString() {
        return InetAddresses.toAddrString(address.getAddress()) + "/" + mask;
    }

    public static IpPrefix<Ipv4Address> fromIpv4(String ip) {
        final String[] tokens = ip.split("/");
        final IpAddress ipAddress = IpAddress.fromIp(tokens[0]);
        final int mask = Integer.parseInt(tokens[1]);

        if (ipAddress instanceof Ipv4Address) {
            Assert.isTrue(mask >= 0 && mask <= MAX_IPV4_MASK,
                    () -> "Mask must be in range <0;" + MAX_IPV4_MASK + ">");

            return new IpPrefix<>((Ipv4Address) ipAddress, mask);
        }
        throw new IllegalArgumentException("Invalid ipv4 " + ip);
    }

    public static IpPrefix<Ipv6Address> fromIpv6(String ip) {
        final String[] tokens = ip.split("/");
        final IpAddress ipAddress = IpAddress.fromIp(tokens[0]);
        final int mask = Integer.parseInt(tokens[1]);

        if (ipAddress instanceof Ipv6Address) {
            Assert.isTrue(mask >= 0 && mask <= MAX_IPV6_MASK,
                    () -> "Mask must be in range <0;" + MAX_IPV6_MASK + ">");
            return new IpPrefix<>((Ipv6Address) ipAddress, mask);
        }
        throw new IllegalArgumentException("Invalid ipv6 " + ip);
    }

}
