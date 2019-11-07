package io.nixer.nixerplugin.core.detection.filter.ip;

import java.io.File;
import java.io.IOException;

import io.nixer.nixerplugin.core.domain.ip.IpLookup;
import io.nixer.nixerplugin.core.domain.ip.IpLookupFactory;
import io.nixer.nixerplugin.core.domain.ip.IpLookup;
import io.nixer.nixerplugin.core.domain.ip.IpLookupFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Configuration
@EnableConfigurationProperties({IpFilterProperties.class})
@ConditionalOnProperty(prefix = "nixer.filter.ip", name = "enabled", havingValue = "true")
public class IpMetadataConfiguration {

    @Bean
    public IpMetadataFilter ipMetadataFilter(IpFilterProperties ipFilterProperties) throws IOException {
        final File ipPrefixFile = ResourceUtils.getFile(ipFilterProperties.getIpPrefixesPath());
        final IpLookup ipLookup = new IpLookupFactory().ipLookup(ipPrefixFile);

        return new IpMetadataFilter(ipLookup);
    }
}
