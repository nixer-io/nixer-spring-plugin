package eu.xword.nixer.nixerplugin.pwned;

import java.io.FileNotFoundException;

import eu.xword.nixer.bloom.BloomFilterCheck;
import eu.xword.nixer.nixerplugin.pwned.check.PwnedCredentialsChecker;
import eu.xword.nixer.nixerplugin.pwned.filter.PwnedCredentialsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

/**
 * Created on 18/09/2019.
 *
 * @author gcwiak
 */
@Configuration
public class PwnedCheckAutoConfiguration {

    @Bean
    public PwnedCredentialsFilter pwnedCredentialsFilter(final PwnedCredentialsChecker pwnedCredentialsChecker) {
        return new PwnedCredentialsFilter(pwnedCredentialsChecker);
    }

    @Bean
    public BloomFilterCheck bloomFilter(final PwnedCheckProperties pwnedCheckProperties) throws FileNotFoundException {
        return BloomFilterCheck.hashingBeforeCheck(
                ResourceUtils.getFile(pwnedCheckProperties.getPwnedFilePath()).toPath()
        );
    }
}
