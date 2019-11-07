package io.nixer.nixerplugin.pwned;

import java.io.FileNotFoundException;

import io.nixer.bloom.check.BloomFilterCheck;
import io.nixer.nixerplugin.core.NixerAutoConfiguration;
import io.nixer.nixerplugin.core.metrics.MetricsFactory;
import io.nixer.nixerplugin.pwned.check.PwnedCredentialsChecker;
import io.nixer.nixerplugin.pwned.filter.PwnedCredentialsFilter;
import io.nixer.nixerplugin.pwned.metrics.PwnedPasswordMetricsReporter;
import io.nixer.nixerplugin.pwned.check.PwnedCredentialsChecker;
import io.nixer.nixerplugin.pwned.filter.PwnedCredentialsFilter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

/**
 * Created on 18/09/2019.
 *
 * @author gcwiak
 */
@Configuration
@EnableConfigurationProperties(value = {PwnedCheckProperties.class})
@AutoConfigureOrder(NixerAutoConfiguration.ORDER + 1)
public class PwnedCheckAutoConfiguration {

    @Bean
    public PwnedCredentialsFilter pwnedCredentialsFilter(final PwnedCheckProperties pwnedCheckProperties,
                                                         final PwnedCredentialsChecker pwnedCredentialsChecker) {

        return new PwnedCredentialsFilter(pwnedCheckProperties.getPasswordParameter(), pwnedCredentialsChecker);
    }

    @Bean
    public PwnedCredentialsChecker pwnedCredentialsChecker(final BloomFilterCheck pwnedFilter,
                                                           final PwnedCheckProperties pwnedCheckProperties,
                                                           final PwnedPasswordMetricsReporter pwnedPasswordMetricsReporter) {
        return new PwnedCredentialsChecker(
                pwnedFilter,
                pwnedCheckProperties.getMaxPasswordLength(),
                pwnedPasswordMetricsReporter
        );
    }

    @Bean
    public BloomFilterCheck bloomFilter(final PwnedCheckProperties pwnedCheckProperties) throws FileNotFoundException {
        return BloomFilterCheck.hashingBeforeCheck(
                ResourceUtils.getFile(pwnedCheckProperties.getPwnedFilePath()).toPath()
        );
    }

    @Bean
    public PwnedPasswordMetricsReporter pwnedPasswordMetricsReporter(MetricsFactory metricsFactory) {
        return PwnedPasswordMetricsReporter.create(metricsFactory);
    }

}
