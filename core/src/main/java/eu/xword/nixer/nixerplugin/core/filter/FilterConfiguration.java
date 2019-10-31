package eu.xword.nixer.nixerplugin.core.filter;

import java.util.List;
import java.util.Optional;

import eu.xword.nixer.nixerplugin.core.filter.behavior.Behavior;
import eu.xword.nixer.nixerplugin.core.filter.behavior.BehaviorEndpoint;
import eu.xword.nixer.nixerplugin.core.filter.behavior.BehaviorProvider;
import eu.xword.nixer.nixerplugin.core.filter.behavior.BehaviorProviderBuilder;
import eu.xword.nixer.nixerplugin.core.filter.behavior.BehaviorRegistry;
import eu.xword.nixer.nixerplugin.core.filter.behavior.BehaviorsProperties;
import eu.xword.nixer.nixerplugin.core.filter.behavior.LogBehavior;
import eu.xword.nixer.nixerplugin.core.ip.IpLookupConfiguration;
import eu.xword.nixer.nixerplugin.core.registry.GlobalCredentialStuffingRegistry;
import eu.xword.nixer.nixerplugin.core.registry.IpOverLoginThresholdRegistry;
import eu.xword.nixer.nixerplugin.core.registry.UserAgentOverLoginThresholdRegistry;
import eu.xword.nixer.nixerplugin.core.registry.UsernameOverLoginThresholdRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(value = {FilterProperties.class, BehaviorsProperties.class})
@Import({
        FilterConfiguration.IpThresholdFilter.class,
        FilterConfiguration.UsernameThresholdFilter.class,
        FilterConfiguration.UserAgentThresholdFilter.class,
        IpLookupConfiguration.class,
})
public class FilterConfiguration {

    private final Log logger = LogFactory.getLog(getClass());

    @Bean
    public BehaviorExecutionFilter executionFilter(FilterProperties filterProperties, BehaviorProvider behaviorProvider, GlobalCredentialStuffingRegistry credentialStuffing) {
        final BehaviorExecutionFilter executionFilter = new BehaviorExecutionFilter(behaviorProvider, credentialStuffing);
        if (filterProperties.isDryRun()) {
            logger.warn("Filters dry-run mode is enabled");
        }
        executionFilter.setDryRun(filterProperties.isDryRun());
        return executionFilter;
    }

    @Bean
    public BehaviorRegistry behaviorRegistry(List<Behavior> behaviors) {
        final BehaviorRegistry behaviorRegistry = new BehaviorRegistry();

        behaviors.forEach(behaviorRegistry::register);

        return behaviorRegistry;
    }

    @Bean
    public LogBehavior logBehavior(BehaviorsProperties behaviorsProperties) {
        final BehaviorsProperties.LogBehaviorProperties logProperties = behaviorsProperties.getLog();

        final LogBehavior logBehavior = new LogBehavior();
        logBehavior.setIncludeHeaders(logProperties.isIncludeHeaders());
        logBehavior.setIncludeMetadata(logProperties.isIncludeMetadata());
        logBehavior.setIncludeUserInfo(logProperties.isIncludeUserInfo());
        logBehavior.setIncludeQueryString(logProperties.isIncludeQueryString());

        return logBehavior;
    }

    @Bean
    public BehaviorProvider buildBehaviorProvider(BehaviorRegistry behaviorRegistry, @Autowired(required = false) BehaviorProviderConfigurer configurer) {
        final BehaviorProviderBuilder builder = BehaviorProviderBuilder.builder(behaviorRegistry);

        Optional.ofNullable(configurer)
                .orElse(defaultRuleConfigurer())
                .configure(builder);

        return builder.build();
    }

    /*
        Ideally we would set bean name prefix for beans inside nested configuration classes.

        Apparently spring doesn't support that.
     */
    @Configuration
    static class UserAgentThresholdFilter {

        @Bean
        public UserAgentFailedLoginOverThresholdFilter userAgentFilter() {
            return new UserAgentFailedLoginOverThresholdFilter(userAgentRegistry());
        }

        @Bean
        public UserAgentOverLoginThresholdRegistry userAgentRegistry() {
            return new UserAgentOverLoginThresholdRegistry();
        }
    }

    @Configuration
    static class UsernameThresholdFilter {

        @Bean
        public UsernameFailedLoginOverThresholdFilter usernameFilter() {
            return new UsernameFailedLoginOverThresholdFilter(usernameRegistry());
        }

        @Bean
        public UsernameOverLoginThresholdRegistry usernameRegistry() {
            return new UsernameOverLoginThresholdRegistry();
        }
    }

    @Configuration
    static class IpThresholdFilter {

        @Bean
        public IpFailedLoginOverThresholdFilter ipFilter() {
            return new IpFailedLoginOverThresholdFilter(ipRegistry());
        }

        @Bean
        public IpOverLoginThresholdRegistry ipRegistry() {
            return new IpOverLoginThresholdRegistry();
        }
    }


    @Bean
    public GlobalCredentialStuffingRegistry credentialStuffing() {
        return new GlobalCredentialStuffingRegistry();
    }

    private BehaviorProviderConfigurer defaultRuleConfigurer() {
        return (it) -> {
            logger.warn("Custom BehaviorProviderConfigurer bean not found. Behaviors are not configured.");
            return it;
        };
    }

    @Bean
    public BehaviorEndpoint behaviorEndpoint(BehaviorProvider behaviorProvider, BehaviorRegistry behaviorRegistry) {
        return new BehaviorEndpoint(behaviorProvider, behaviorRegistry);
    }

    public interface BehaviorProviderConfigurer {
        BehaviorProviderBuilder configure(BehaviorProviderBuilder behaviorProviderBuilder);
    }
}
