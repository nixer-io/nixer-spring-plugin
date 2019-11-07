package io.nixer.nixerplugin.core.domain.ip;

import java.net.InetAddress;

import com.google.common.net.InetAddresses;
import io.nixer.nixerplugin.core.domain.ip.net.IpAddress;
import io.nixer.nixerplugin.core.domain.ip.net.Ipv4Address;
import io.nixer.nixerplugin.core.domain.ip.net.Ipv6Address;
import io.nixer.nixerplugin.core.domain.ip.tree.IpTree;
import org.springframework.util.Assert;

public class IpLookup {

    private final IpTree<Ipv4Address> ipv4PrefixTree;
    private final IpTree<Ipv6Address> ipv6PrefixTree;

    IpLookup(final IpTree<Ipv4Address> ipv4PrefixTree, final IpTree<Ipv6Address> ipv6PrefixTree) {
        Assert.notNull(ipv4PrefixTree, "ipv4PrefixTree must not be null");
        this.ipv4PrefixTree = ipv4PrefixTree;

        Assert.notNull(ipv6PrefixTree, "ipv6PrefixTree must not be null");
        this.ipv6PrefixTree = ipv6PrefixTree;
    }

    public IpAddress lookup(final String ip) {
        Assert.notNull(ip, "Ip must not be null");

        final InetAddress inetAddress = InetAddresses.forString(ip);
        final IpAddress ipAddress = IpAddress.fromInetAddress(inetAddress);
        if (ipAddress instanceof Ipv4Address) {
            return lookupIPv4((Ipv4Address) ipAddress);
        } else if (ipAddress instanceof Ipv6Address) {
            return lookupIPv6((Ipv6Address) ipAddress);
        } else {
            throw new IllegalArgumentException("Unsupported address type " + ip);
        }
    }

    private IpAddress lookupIPv4(Ipv4Address ipAddress) {
        return ipv4PrefixTree.contains(ipAddress) ? ipAddress : null;
    }

    private IpAddress lookupIPv6(Ipv6Address ipAddress) {
        return ipv6PrefixTree.contains(ipAddress) ? ipAddress : null;
    }

}
