package eu.xword.nixer.nixerplugin.ip.tree;

import java.net.InetAddress;

import com.google.common.base.Objects;
import com.google.common.net.InetAddresses;

public class IpPrefix {

    public static final int[] MASKS = {
            0b00000000000000000000000000000000,
            0b10000000000000000000000000000000,
            0b11000000000000000000000000000000,
            0b11100000000000000000000000000000,
            0b11110000000000000000000000000000,
            0b11111000000000000000000000000000,
            0b11111100000000000000000000000000,
            0b11111110000000000000000000000000,
            0b11111111000000000000000000000000,
            0b11111111100000000000000000000000,
            0b11111111110000000000000000000000,
            0b11111111111000000000000000000000,
            0b11111111111100000000000000000000,
            0b11111111111110000000000000000000,
            0b11111111111111000000000000000000,
            0b11111111111111100000000000000000,
            0b11111111111111110000000000000000,
            0b11111111111111111000000000000000,
            0b11111111111111111100000000000000,
            0b11111111111111111110000000000000,
            0b11111111111111111111000000000000,
            0b11111111111111111111100000000000,
            0b11111111111111111111110000000000,
            0b11111111111111111111111000000000,
            0b11111111111111111111111100000000,
            0b11111111111111111111111110000000,
            0b11111111111111111111111111000000,
            0b11111111111111111111111111100000,
            0b11111111111111111111111111110000,
            0b11111111111111111111111111111000,
            0b11111111111111111111111111111100,
            0b11111111111111111111111111111110,
            0b11111111111111111111111111111111,
    };

    private int address;
    private int mask;

    public IpPrefix(final int address, final int mask) {
        this.address = address;
        this.mask = mask;
    }

    public int getAddress() {
        return address;
    }

    public int getMask() {
        return mask;
    }

    public int getNetmask() {
        return maskToNetmask(mask);
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
        return InetAddresses.toAddrString(InetAddresses.fromInteger(address)) + "/" + mask;
    }

    private static final int maskToNetmask(int mask) {
        return MASKS[mask];
    }

    public static IpPrefix fromIp(String ip) {
        final String[] tokens = ip.split("/");

        return new IpPrefix(ipToInt(tokens[0]), Integer.parseInt(tokens[1]));
    }

    private static int ipToInt(String ip) {
        final InetAddress address = InetAddresses.forString(ip);
        return InetAddresses.coerceToInteger(address);
    }

}
