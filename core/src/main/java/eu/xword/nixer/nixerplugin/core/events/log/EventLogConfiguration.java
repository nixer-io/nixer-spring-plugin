package eu.xword.nixer.nixerplugin.core.events.log;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(EventLogProperties.class)
@ConditionalOnProperty(prefix = "nixer.events.log", name = "enabled", havingValue = "true", matchIfMissing = false)
public class EventLogConfiguration {

    @Bean
    public EventLogger loggingEventListener() {
        return new EventLogger();
    }
}
