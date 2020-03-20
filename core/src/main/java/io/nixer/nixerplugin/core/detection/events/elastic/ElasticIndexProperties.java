package io.nixer.nixerplugin.core.detection.events.elastic;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.events.elastic")
public class ElasticIndexProperties {

    /**
     * Whether anomaly events should be logged to Elasticsearch.
     * Requires Elastic Search running.
     */
    private boolean enabled = false;

    /**
     * Elasticsearch index.
     */
    private String index;

    /**
     * Elasticsearch type.
     */
    private String type = "_doc";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(final String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
