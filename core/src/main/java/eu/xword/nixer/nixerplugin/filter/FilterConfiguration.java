package eu.xword.nixer.nixerplugin.filter;

import eu.xword.nixer.nixerplugin.detection.GlobalCredentialStuffing;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviorEndpoint;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviorProvider;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviorRegistry;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviourProviderBuilder;
import eu.xword.nixer.nixerplugin.filter.behavior.CaptchaBehaviour;
import eu.xword.nixer.nixerplugin.filter.behavior.Facts;
import eu.xword.nixer.nixerplugin.ip.IpMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.xword.nixer.nixerplugin.filter.RequestAugmentation.GLOBAL_CREDENTIAL_STUFFING;
import static eu.xword.nixer.nixerplugin.filter.RequestAugmentation.IP_METADATA;

@Configuration
@EnableConfigurationProperties(value = {FilterProperties.class})
public class FilterConfiguration {

    private final Log logger = LogFactory.getLog(getClass());

//    @Bean
//    public UsernameLoginFilter userLockBlockingPolicy(BlockedUserRegistry blockedUserRegistry) {
//        return new UsernameLoginFilter(blockedUserRegistry);
//    }
//
//    @Bean
//    public TemporalIpFilter temporalIpFilter(BlockedIpRegistry blockedIpRegistry) {
//        return new TemporalIpFilter(blockedIpRegistry);
//    }

    @Bean
    public BehaviorExecutionFilter executionFilter(FilterProperties filterProperties, BehaviorProvider behaviorProvider, GlobalCredentialStuffing credentialStuffing) {
        final BehaviorExecutionFilter executionFilter = new BehaviorExecutionFilter(behaviorProvider, credentialStuffing);
        if (filterProperties.isDryRun()) {
            logger.warn("Filters dry-run mode is enabled");
        }
        executionFilter.setDryRun(filterProperties.isDryRun());
        return executionFilter;
    }

    @Bean
    public BehaviorProvider buildBehaviorProvider(BehaviorRegistry behaviorRegistry) {
        return BehaviourProviderBuilder.builder()
                .addRule("blacklistedIp", this::isBlacklistedIp, "blockedError")
                .addRule("credentialStuffingActive", this::isGlobalCredentialStuffing, CaptchaBehaviour.CAPTCHA)
                .build(behaviorRegistry);
    }

    @Bean
    public BehaviorEndpoint behaviorEndpoint(BehaviorProvider behaviorProvider, BehaviorRegistry behaviorRegistry) {
        return new BehaviorEndpoint(behaviorProvider, behaviorRegistry);
    }

    private boolean isBlacklistedIp(Facts facts) {
        IpMetadata ipMetadata = (IpMetadata) facts.getFact(IP_METADATA);
        return ipMetadata != null && ipMetadata.isBlacklisted();
    }

    private boolean isGlobalCredentialStuffing(Facts facts) {
        return Boolean.TRUE.equals(facts.getFact(GLOBAL_CREDENTIAL_STUFFING));
    }
}
