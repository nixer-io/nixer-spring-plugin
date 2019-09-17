package eu.xword.nixer.nixerplugin.ip;

import java.io.File;
import java.io.IOException;

import eu.xword.nixer.nixerplugin.filter.strategy.MitigationStrategy;
import eu.xword.nixer.nixerplugin.ip.net.Ipv4Address;
import eu.xword.nixer.nixerplugin.ip.net.Ipv6Address;
import eu.xword.nixer.nixerplugin.ip.tree.IpTree;
import eu.xword.nixer.nixerplugin.ip.tree.IpTreeBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Configuration
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

    @Bean
    public IpFilter ipFilter(IpLookup ipLookup, MitigationStrategy mitigationStrategy) {
        final IpFilter filter = new IpFilter(ipLookup);
        filter.setMitigationStrategy(mitigationStrategy);
        return filter;
    }
}
