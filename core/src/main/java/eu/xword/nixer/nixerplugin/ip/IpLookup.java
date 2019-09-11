package eu.xword.nixer.nixerplugin.ip;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import com.google.common.net.InetAddresses;
import eu.xword.nixer.nixerplugin.ip.tree.IpTree;
import org.springframework.util.Assert;

public class IpLookup {

    private IpTree ipv4PrefixTree;
    private IpTree ipv6PrefixTree;

    public IpLookup(final IpTree ipv4PrefixTree, final IpTree ipv6PrefixTree) {
        this.ipv4PrefixTree = ipv4PrefixTree;
        this.ipv6PrefixTree = ipv6PrefixTree;
    }

    public IpAddress lookup(String ip) {
        Assert.notNull(ip, "Ip must not be null");

        final InetAddress inetAddress = InetAddresses.forString(ip);
        if (inetAddress instanceof Inet4Address) {
            return lookupIPv4(inetAddress);
        } else if (inetAddress instanceof Inet6Address) {
            return lookupIPv6(inetAddress);
        } else {
            throw new IllegalArgumentException("Unsupported address type " + ip);
        }
    }


    private IpAddress lookupIPv4(InetAddress inetAddress) {

        final int ipv4 = InetAddresses.coerceToInteger(inetAddress);
        return ipv4PrefixTree.contains(ipv4) ? IpAddress.fromInt(ipv4) : null;
    }

    private IpAddress lookupIPv6(InetAddress ipv6) {
//        ipv6PrefixTree.contains(0)
        return null;
    }

}
