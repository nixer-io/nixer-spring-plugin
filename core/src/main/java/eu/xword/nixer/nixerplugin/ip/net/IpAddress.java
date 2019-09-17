package eu.xword.nixer.nixerplugin.ip.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.BitSet;

import com.google.common.net.InetAddresses;

/**
 * Abstraction of Ip address over InetAddress. Could represent both IPv4 and IPv6 addresses.
 */
public class IpAddress {

    private InetAddress address;
    private BitSet bitSet;

    protected IpAddress(final InetAddress address) {
        this.address = address;
        this.bitSet = BitSet.valueOf(address.getAddress());
    }

    public InetAddress getAddress() {
        return address;
    }

    public boolean getBit(int nBit) {
        int index = 8 * (nBit / 8) + 7 - (nBit % 8);
        return bitSet.get(index);
    }

    public static IpAddress fromIp(String ip) {
        final InetAddress address = InetAddresses.forString(ip);

        return fromInetAddress(address);
    }

    public static IpAddress fromInetAddress(InetAddress inetAddress) {
        if (inetAddress instanceof Inet4Address) {
            return new Ipv4Address((Inet4Address) inetAddress);
        }
        if (inetAddress instanceof Inet6Address) {
            return new Ipv6Address((Inet6Address) inetAddress);
        }
        return new IpAddress(inetAddress);
    }

    @Override
    public String toString() {
        return InetAddresses.toAddrString(address);
    }
}
