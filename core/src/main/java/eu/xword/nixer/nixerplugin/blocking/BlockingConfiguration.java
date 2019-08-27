package eu.xword.nixer.nixerplugin.blocking;

import eu.xword.nixer.nixerplugin.blocking.policies.MitigationStrategy;
import eu.xword.nixer.nixerplugin.blocking.policies.RedirectMitigationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlockingConfiguration {

    //    @Bean
//    public UserLockBlockingPolicy userLockBlockingPolicy(MitigationStrategy mitigationStrategy) {
//        return new UserLockBlockingPolicy(mitigationStrategy);
//    }
//
//    @Bean
//    public SourceIpBlockingPolicy ipBlockingPolicy(MitigationStrategy mitigationStrategy) {
//        return new SourceIpBlockingPolicy(mitigationStrategy);
//    }

    @Bean
    public MitigationStrategy mitigationStrategy() {
        return new RedirectMitigationStrategy("/login?blockedError");
    }
}
