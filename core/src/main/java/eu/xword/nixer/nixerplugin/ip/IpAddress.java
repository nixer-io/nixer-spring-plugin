package eu.xword.nixer.nixerplugin.ip;

import java.net.InetAddress;

import com.google.common.net.InetAddresses;

public class IpAddress {
    private int address;

    private IpAddress(final int address) {
        this.address = address;
    }

    public static IpAddress fromIp(String text) {
        final InetAddress address = InetAddresses.forString(text);
        final int number = InetAddresses.coerceToInteger(address);

        return new IpAddress(number);
    }

    public static IpAddress fromInt(int ip) {
        return new IpAddress(ip);
    }

    public int asInteger() {
        return address;
    }

    public String asString() {
        return InetAddresses.fromInteger(this.address).toString();
    }


}
