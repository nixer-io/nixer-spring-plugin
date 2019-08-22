package eu.xword.nixer.nixerplugin.captcha;

import java.time.Duration;

import eu.xword.nixer.nixerplugin.captcha.error.FallbackMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "recaptcha")
@Component
public class RecaptchaProperties {

    private String verifyUrl;

    private Http http = new Http();
    private RecaptchaKeys key = new RecaptchaKeys();

    private String param = "g-recaptcha-response";

    private FallbackMode fallback = FallbackMode.FAIL;

    private BlockingProperties blocking = new BlockingProperties();

    public String getVerifyUrl() {
        return verifyUrl;
    }

    public void setVerifyUrl(final String verifyUrl) {
        this.verifyUrl = verifyUrl;
    }

    public RecaptchaKeys getKey() {
        return key;
    }

    public void setKey(final RecaptchaKeys key) {
        this.key = key;
    }

    public String getParam() {
        return param;
    }

    public void setParam(final String param) {
        this.param = param;
    }

    public Http getHttp() {
        return http;
    }

    public void setHttp(final Http http) {
        this.http = http;
    }

    public FallbackMode getFallback() {
        return fallback;
    }

    public void setFallback(final FallbackMode fallback) {
        this.fallback = fallback;
    }

    public BlockingProperties getBlocking() {
        return blocking;
    }

    public void setBlocking(final BlockingProperties blocking) {
        this.blocking = blocking;
    }

    public static class Http {
        private Timeout timeout = new Timeout();
        private int maxConnections = 10;

        public Timeout getTimeout() {
            return timeout;
        }

        public int getMaxConnections() {
            return maxConnections;
        }

        public void setTimeout(final Timeout timeout) {
            this.timeout = timeout;
        }

        public void setMaxConnections(final int maxConnections) {
            this.maxConnections = maxConnections;
        }
    }

    public static class Timeout {
        private int connect = 1_000;
        private int read = 1_000;
        private int connectionRequest = 1_000;

        public int getConnect() {
            return connect;
        }

        public void setConnect(final int connect) {
            this.connect = connect;
        }

        public int getRead() {
            return read;
        }

        public void setRead(final int read) {
            this.read = read;
        }

        public int getConnectionRequest() {
            return connectionRequest;
        }

        public void setConnectionRequest(final int connectionRequest) {
            this.connectionRequest = connectionRequest;
        }
    }

    public static class RecaptchaKeys {
        private String site;
        private String secret;

        public String getSite() {
            return site;
        }

        public void setSite(final String site) {
            this.site = site;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(final String secret) {
            this.secret = secret;
        }
    }

    public static class BlockingProperties {
        private static final int DEFAULT_MAX_ATTEMPTS = 4;
        private static final Duration DEFAULT_BLOCKING_DURATION = Duration.ofHours(1);

        private boolean enabled = false;
        private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
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
