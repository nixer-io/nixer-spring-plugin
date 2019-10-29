package eu.xword.nixer.nixerplugin.core.ip;

import java.io.File;
import java.io.IOException;

import eu.xword.nixer.nixerplugin.core.filter.IpFilter;
import eu.xword.nixer.nixerplugin.core.ip.net.Ipv4Address;
import eu.xword.nixer.nixerplugin.core.ip.net.Ipv6Address;
import eu.xword.nixer.nixerplugin.core.ip.tree.IpTree;
import eu.xword.nixer.nixerplugin.core.ip.tree.IpTreeBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;

@EnableConfigurationProperties({IpFilterProperties.class})
@ConditionalOnProperty(prefix = "nixer.filter.ip", name = "enabled", havingValue = "true")
public class IpFilterConfiguration {

    @Bean
    public IpLookup ipLookup(IpFilterProperties ipFilterProperties) throws IOException {
        final File ipPrefixFile = ResourceUtils.getFile(ipFilterProperties.getIpPrefixesPath());

        final IpTree<Ipv4Address> ipv4Tree = IpTreeBuilder.fromFile(ipPrefixFile)
                .buildIpv4Tree();

        final IpTree<Ipv6Address> ipv6Tree = IpTreeBuilder.fromFile(ipPrefixFile)
                .buildIpv6Tree();

        return new IpLookup(ipv4Tree, ipv6Tree);
    }


    //todo: discuss naming filters and behaviors

    @Bean("ipRangeFilter")
    public IpFilter ipFilter(IpLookup ipLookup) {
        return new IpFilter(ipLookup);
    }

}
