package eu.xword.nixer.nixerplugin.core.detection.filter.behavior;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.behaviors")
public class BehaviorsProperties {

    private LogBehaviorProperties log = new LogBehaviorProperties();

    public LogBehaviorProperties getLog() {
        return log;
    }

    public void setLog(final LogBehaviorProperties log) {
        this.log = log;
    }

    /**
     * Defines properties of log behavior
     */
    public static class LogBehaviorProperties {

        /**
         * Whether metadata about request should be logged. Includes pwned check results, ip lookup match, thresholds exceeded
         */
        private boolean includeMetadata = true;

        /**
         * Whether query part of uri should be logged
         */
        private boolean includeQueryString = false;

        /**
         * Whether http headers should be logged
         */
        private boolean includeHeaders = true;

        /**
         * Whether user info should be logged. Includes ip, username, session_id
         */
        private boolean includeUserInfo = true;

        public boolean isIncludeMetadata() {
            return includeMetadata;
        }

        public void setIncludeMetadata(final boolean includeMetadata) {
            this.includeMetadata = includeMetadata;
        }

        public boolean isIncludeQueryString() {
            return includeQueryString;
        }

        public void setIncludeQueryString(final boolean includeQueryString) {
            this.includeQueryString = includeQueryString;
        }

        public boolean isIncludeHeaders() {
            return includeHeaders;
        }

        public void setIncludeHeaders(final boolean includeHeaders) {
            this.includeHeaders = includeHeaders;
        }

        public boolean isIncludeUserInfo() {
            return includeUserInfo;
        }

        public void setIncludeUserInfo(final boolean includeUserInfo) {
            this.includeUserInfo = includeUserInfo;
        }
    }
}
