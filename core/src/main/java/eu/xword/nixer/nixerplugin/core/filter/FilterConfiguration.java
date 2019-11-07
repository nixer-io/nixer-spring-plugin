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
import eu.xword.nixer.nixerplugin.core.registry.GlobalCredentialStuffingRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(value = {FilterProperties.class, BehaviorsProperties.class})
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
