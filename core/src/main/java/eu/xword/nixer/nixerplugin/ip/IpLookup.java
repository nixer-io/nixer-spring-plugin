package eu.xword.nixer.nixerplugin.ip;

import java.net.InetAddress;

import com.google.common.net.InetAddresses;
import eu.xword.nixer.nixerplugin.ip.net.IpAddress;
import eu.xword.nixer.nixerplugin.ip.net.Ipv4Address;
import eu.xword.nixer.nixerplugin.ip.net.Ipv6Address;
import eu.xword.nixer.nixerplugin.ip.tree.IpTree;
import org.springframework.util.Assert;

public class IpLookup {

    private IpTree<Ipv4Address> ipv4PrefixTree;
    private IpTree<Ipv6Address> ipv6PrefixTree;

    public IpLookup(final IpTree<Ipv4Address> ipv4PrefixTree, final IpTree<Ipv6Address> ipv6PrefixTree) {
        this.ipv4PrefixTree = ipv4PrefixTree;
        this.ipv6PrefixTree = ipv6PrefixTree;
    }

    public IpAddress lookup(String ip) {
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
