package eu.xword.nixer.nixerplugin.pwned;

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
    public PwnedCredentialsFilter pwnedCredentialsFilter() {
        return new PwnedCredentialsFilter();
    }
}
