package eu.xword.nixer.nixerplugin.ip;

import java.io.File;
import java.io.IOException;

import eu.xword.nixer.nixerplugin.ip.tree.IpTree;
import eu.xword.nixer.nixerplugin.ip.tree.IpTreeBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Configuration
@EnableConfigurationProperties({IpLookupProperties.class})
@ConditionalOnProperty(prefix = "nixer.ip-lookup", name = "enabled", havingValue = "true")
public class IpLookupConfiguration {

    @Bean
    public IpLookup ipLookup(IpLookupProperties ipLookupProperties) throws IOException {
        final File ipPrefixFile = ResourceUtils.getFile(ipLookupProperties.getIpPrefixesPath());

        final IpTree ipv4Tree = IpTreeBuilder.fromFile(ipPrefixFile)
                .buildIpv4Tree();

        final IpTree ipv6Tree = IpTreeBuilder.fromFile(ipPrefixFile)
                .buildIpv6Tree();

        return new IpLookup(ipv4Tree, ipv6Tree);
    }

    @Bean
    public IpFilter ipFilter(IpLookup ipLookup) {
        return new IpFilter(ipLookup);
    }
}
