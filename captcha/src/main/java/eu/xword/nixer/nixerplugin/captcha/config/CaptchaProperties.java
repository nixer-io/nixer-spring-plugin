package eu.xword.nixer.nixerplugin.captcha.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * {@link ConfigurationProperties} for configuring captcha protection.
 */
@Component
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {

    private BlockingProperties blocking = new BlockingProperties();

    private MetricsProperties metrics = new MetricsProperties();

    public BlockingProperties getBlocking() {
        return blocking;
    }

    public void setBlocking(final BlockingProperties blocking) {
        this.blocking = blocking;
    }

    public MetricsProperties getMetrics() {
        return metrics;
    }

    public void setMetrics(final MetricsProperties metrics) {
        this.metrics = metrics;
    }

    public static class MetricsProperties {
        public static final boolean DEFAULT = true;

        /**
         * Whether captcha metrics should be reported or not
         */
        private boolean enabled = DEFAULT;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class BlockingProperties {
        private static final int DEFAULT_MAX_ATTEMPTS = 4;
        private static final Duration DEFAULT_BLOCKING_DURATION = Duration.ofHours(1);

        /**
         * Whether blocking solving captcha is enabled
         */
        private boolean enabled = false;
        /**
         * Max number of failed attempts before user is blocked
         */
        private int maxAttempts = DEFAULT_MAX_ATTEMPTS;

        /**
         * Duration for which captcha solve attempts are blocked
         */
        private Duration duration = DEFAULT_BLOCKING_DURATION;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(final int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(final String duration) {
            this.duration = Duration.parse(duration);
        }
    }
}
