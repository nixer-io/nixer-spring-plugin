package io.nixer.nixerplugin.core.fingerprint;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 01/05/2020.
 *
 * @author Grzegorz Cwiak
 */
@Configuration
@EnableConfigurationProperties({FingerprintProperties.class})
@ConditionalOnProperty(prefix = "nixer.fingerprint", name = "enabled", havingValue = "true")
public class FingerprintConfiguration {

    @Bean
    public FingerprintAssuringFilter fingerprintCookieFilter(FingerprintProperties fingerprintProperties,
                                                             FingerprintGenerator fingerprintGenerator) {
        return new FingerprintAssuringFilter(fingerprintProperties.getCookieName(), fingerprintGenerator);
    }

    @Bean
    public FingerprintGenerator fingerprintGenerator() {
        return new FingerprintGenerator();
    }
}
