package eu.xword.nixer.nixerplugin.core.ip;

import java.io.File;
import java.io.IOException;

import eu.xword.nixer.nixerplugin.core.filter.IpFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Configuration
@EnableConfigurationProperties({IpFilterProperties.class})
@ConditionalOnProperty(prefix = "nixer.filter.ip", name = "enabled", havingValue = "true")
public class IpLookupConfiguration {

    @Bean("ipRangeFilter")
    public IpFilter ipFilter(IpFilterProperties ipFilterProperties) throws IOException {
        final File ipPrefixFile = ResourceUtils.getFile(ipFilterProperties.getIpPrefixesPath());
        final IpLookup ipLookup = new IpLookupFactory().ipLookup(ipPrefixFile);

        return new IpFilter(ipLookup);
    }
}
