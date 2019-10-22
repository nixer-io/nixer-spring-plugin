package eu.xword.nixer.nixerplugin.pwned;

import eu.xword.nixer.nixerplugin.pwned.metrics.PwnedCheckMetricsConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created on 24/09/2019.
 *
 * @author gcwiak
 */
@ConfigurationProperties(prefix = "nixer.pwned.check")
public class PwnedCheckProperties {

    /**
     * Indicates pwned-check functionality is enabled.
     * Used by {@link PwnedCheckAutoConfiguration}, kept here for documentation purposes.
     */
    private boolean enabled;

    /**
     * The HTTP parameter to look for the password when performing the check.
     */
    // FIXME this duplicates the parameter set in Spring's FormLoginConfigurer.
    // Think how it can be reused.
    private String passwordParameter = "password";

    /**
     * Location of leaked credentials data file
     */
    private String pwnedFilePath;

    /**
     * Limit for length of the checked password.
     * Mitigates the risk of flooding the system with unnaturally long passwords.
     * Passwords longer than this limit will not be checked and treated as false match.
     */
    private int maxPasswordLength = 50;

    private PwnedCheckMetricsProperties metrics;

    public static class PwnedCheckMetricsProperties {

        /**
         * Indicates pwned-check metrics functionality is enabled.
         * Used in {@link PwnedCheckMetricsConfiguration}, kept here for documentation purposes.
         */
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }
    }

    public PwnedCheckMetricsProperties getMetrics() {
        return metrics;
    }

    public void setMetrics(final PwnedCheckMetricsProperties metrics) {
        this.metrics = metrics;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }

    public void setPasswordParameter(final String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    public String getPwnedFilePath() {
        return pwnedFilePath;
    }

    public void setPwnedFilePath(final String pwnedFilePath) {
        this.pwnedFilePath = pwnedFilePath;
    }

    public int getMaxPasswordLength() {
        return maxPasswordLength;
    }

    public void setMaxPasswordLength(final int maxPasswordLength) {
        this.maxPasswordLength = maxPasswordLength;
    }
}
