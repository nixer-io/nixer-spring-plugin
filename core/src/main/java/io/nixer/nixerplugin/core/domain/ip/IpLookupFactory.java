package io.nixer.nixerplugin.core.domain.ip;

import java.io.File;
import java.io.IOException;

import io.nixer.nixerplugin.core.domain.ip.net.Ipv4Address;
import io.nixer.nixerplugin.core.domain.ip.net.Ipv6Address;
import io.nixer.nixerplugin.core.domain.ip.tree.IpTree;
import io.nixer.nixerplugin.core.domain.ip.tree.IpTreeBuilder;
import org.springframework.util.Assert;

/**
 * Factory class for {@link IpLookup}
 */
public class IpLookupFactory {

    public IpLookup ipLookup(final File file) throws IOException {
        Assert.notNull(file, "File must not be null");

        final IpTree<Ipv4Address> ipv4Tree = IpTreeBuilder.fromFile(file)
                .buildIpv4Tree();
        final IpTree<Ipv6Address> ipv6Tree = IpTreeBuilder.fromFile(file)
                .buildIpv6Tree();

        return new IpLookup(ipv4Tree, ipv6Tree);
    }
}
