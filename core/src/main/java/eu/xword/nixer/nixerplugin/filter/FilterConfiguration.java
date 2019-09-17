package eu.xword.nixer.nixerplugin.filter;

import eu.xword.nixer.nixerplugin.filter.strategy.MitigationStrategy;
import eu.xword.nixer.nixerplugin.filter.strategy.RedirectBehavior;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

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
        return new RedirectBehavior("/login?blockedError");
    }

}
