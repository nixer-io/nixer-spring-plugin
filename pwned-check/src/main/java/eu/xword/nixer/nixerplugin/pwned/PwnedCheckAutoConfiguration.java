package eu.xword.nixer.nixerplugin.pwned;

import java.nio.file.Paths;

import com.google.common.hash.Funnels;
import eu.xword.nixer.bloom.BloomFilter;
import eu.xword.nixer.bloom.FileBasedBloomFilter;
import eu.xword.nixer.nixerplugin.pwned.check.PwnedCredentialsChecker;
import eu.xword.nixer.nixerplugin.pwned.filter.PwnedCredentialsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public BloomFilter<byte[]> bloomFilter() {
        return FileBasedBloomFilter.open(
                Paths.get(""), // FIXME config.pwnedBloomFile()
                Funnels.byteArrayFunnel()
        );
    }
}
