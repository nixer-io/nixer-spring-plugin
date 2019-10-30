package eu.xword.nixer.nixerplugin.core.events.elastic;

import io.searchbox.client.JestClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({ElasticIndexProperties.class})
@ConditionalOnProperty(prefix = "nixer.events.elastic", name = "enabled", havingValue = "true")
public class ElasticLoggingAutoConfiguration {

    @Bean
    public ElasticIndexer elasticEventsIndexer(JestClient jestClient, ElasticIndexProperties elasticProps) {
        return new ElasticIndexer(jestClient, elasticProps.getIndex(), elasticProps.getType());
    }

}
