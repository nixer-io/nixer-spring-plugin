package eu.xword.nixer.nixerplugin.core.ip;

import java.io.File;
import java.io.IOException;

import eu.xword.nixer.nixerplugin.core.ip.net.Ipv4Address;
import eu.xword.nixer.nixerplugin.core.ip.net.Ipv6Address;
import eu.xword.nixer.nixerplugin.core.ip.tree.IpTree;
import eu.xword.nixer.nixerplugin.core.ip.tree.IpTreeBuilder;

class IpLookupFactory {

    IpLookup ipLookup(File file) throws IOException {
        final IpTree<Ipv4Address> ipv4Tree = IpTreeBuilder.fromFile(file)
                .buildIpv4Tree();
        final IpTree<Ipv6Address> ipv6Tree = IpTreeBuilder.fromFile(file)
                .buildIpv6Tree();

        return new IpLookup(ipv4Tree, ipv6Tree);
    }
}
