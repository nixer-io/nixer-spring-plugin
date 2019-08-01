package eu.xword.nixer.nixerplugin;

import eu.xword.nixer.nixerplugin.blocking.policies.MitigationStrategy;
import eu.xword.nixer.nixerplugin.blocking.policies.RedirectStrategy;
import eu.xword.nixer.nixerplugin.blocking.policies.SourceIpBlockingPolicy;
import eu.xword.nixer.nixerplugin.blocking.policies.UserLockBlockingPolicy;
import eu.xword.nixer.nixerplugin.metrics.LoginMetricsReporter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({NixerProperties.class})
@Configuration
@Import(StigmaConfiguration.class)
public class NixerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LoginMetricsReporter.class)
    @ConditionalOnClass(MeterRegistry.class)
    public LoginMetricsReporter loginMetrics(MeterRegistry meterRegistry) {
        return new LoginMetricsReporter(meterRegistry);
    }


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
    public MitigationStrategy mitigationStrategy(){
        return new RedirectStrategy("/login?blockedError");
    }
}
