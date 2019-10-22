package eu.xword.nixer.nixerplugin.filter;

import java.util.List;
import java.util.Optional;

import eu.xword.nixer.nixerplugin.filter.behavior.Behavior;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviorEndpoint;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviorProvider;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviorRegistry;
import eu.xword.nixer.nixerplugin.filter.behavior.BehaviourProviderBuilder;
import eu.xword.nixer.nixerplugin.registry.GlobalCredentialStuffingRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {FilterProperties.class})
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
    public BehaviorProvider buildBehaviorProvider(BehaviorRegistry behaviorRegistry, @Autowired(required = false) BehaviorProviderConfigurer configurer) {
        final BehaviourProviderBuilder builder = BehaviourProviderBuilder.builder();

        Optional.ofNullable(configurer)
                .orElse(defaultRuleConfigurer())
                .configure(builder);

        return builder.build(behaviorRegistry);
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
        BehaviourProviderBuilder configure(BehaviourProviderBuilder behaviourProviderBuilder);
    }
}
