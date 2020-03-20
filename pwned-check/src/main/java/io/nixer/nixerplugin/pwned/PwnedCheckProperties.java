package io.nixer.nixerplugin.pwned;

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
     */
    private boolean enabled;

    /**
     * The HTTP parameter to look for the password when performing the check.
     * <br>
     * <b>Must follow the value of</b> <code>org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter#passwordParameter</code>
     * which is might be changed by <code>org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer<code>.
     * <br>
     * Defaults to the Spring Security default value.
     */
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
