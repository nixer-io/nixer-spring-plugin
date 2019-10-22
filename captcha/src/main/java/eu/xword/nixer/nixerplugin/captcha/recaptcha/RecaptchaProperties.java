package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * {@link ConfigurationProperties} for configuring ReCaptcha.
 */
@ConfigurationProperties(prefix = "nixer.captcha.recaptcha")
public class RecaptchaProperties {

    /**
     * Http url of Google service used to verify captcha
     */
    private String verifyUrl;

    private Http http = new Http();
    private RecaptchaKeys key = new RecaptchaKeys();

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

    public Http getHttp() {
        return http;
    }

    public void setHttp(final Http http) {
        this.http = http;
    }

    public static class Http {
        private Timeout timeout = new Timeout();

        /**
         * Maximum number of connection established
         */
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
        /**
         * Http connection timeout
         */
        private int connect = 1_000;
        /**
         * Socket read timeout
         */
        private int read = 1_000;
        /**
         * Timeout for request for connection from pool
         */
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

        /**
         * Site key for Google Recaptcha API
         */
        private String site;
        /**
         * Secret key for Google Recaptcha API
         */
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
}
