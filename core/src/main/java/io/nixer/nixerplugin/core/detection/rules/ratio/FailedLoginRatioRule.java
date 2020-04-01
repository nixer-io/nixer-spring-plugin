package io.nixer.nixerplugin.core.detection.rules.ratio;

import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioActivationEvent;
import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioDeactivationEvent;
import io.nixer.nixerplugin.core.detection.rules.EventEmitter;
import io.nixer.nixerplugin.core.detection.rules.LoginRule;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;
import io.nixer.nixerplugin.core.login.inmemory.LoginMetric;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Emits activation or deactivation failed-login-ratio events basing on the value of the failed-login-ratio metric.
 * <br/>
 * The metric is calculated as follows:
 * <pre>
 *     failed-login-ratio = (100 * number or failed logins) / (number of all logins)
 * </pre>
 *
 * The behavior is defined by the following thresholds:
 *
 * <p>
 *     {@link #activationLevel} - value of failed-login-ratio metric above which an activation event will be generated.
 * </p>
 * <p>
 *     {@link #deactivationLevel} - percentage value of failed-login-ratio metric below which a deactivation event will be generated.
 * </p>
 *
 * Activation and deactivation levels create hysteresis to better cope with credential stuffing and also to prevent too frequent
 * activation/deactivation events.
 */
public class FailedLoginRatioRule implements LoginRule {

    private static final Log logger = LogFactory.getLog(FailedLoginRatioRule.class);

    private final LoginMetric loginMetric;
    private final int activationLevel;
    private final int deactivationLevel;
    private final int minimumSampleSize;

    /**
     *
     * @param activationLevel [0 - 100] integer represents percentage of failed-to-all login requests needed for activation
     * @param deactivationLevel [0 - 100] integer represents percentage of failed-to-all login requests needed for deactivation
     * @param minimumSampleSize minimum number of login requests needed for rule to act
     */
    public FailedLoginRatioRule(final LoginMetric loginMetric, int activationLevel, int deactivationLevel, int minimumSampleSize) {
        Assert.notNull(loginMetric, "LoginMetric must not be null");
        this.loginMetric = loginMetric;

        if (activationLevel < deactivationLevel) {
            throw new IllegalStateException(String.format("Activation level (%d) must be equal or bigger than deactivation level (%d)",
                    activationLevel,
                    deactivationLevel));
        }
        if (activationLevel < 0 || activationLevel > 100 || deactivationLevel < 0 || deactivationLevel > 100) {
            throw new IllegalStateException(String.format("Activation (%d) and deactivation (%d) levels must be within 0-100 range",
                    activationLevel,
                    deactivationLevel));
        }
        this.activationLevel = activationLevel;
        this.deactivationLevel = deactivationLevel;
        this.minimumSampleSize = minimumSampleSize;
    }

    @Override
    public void execute(final LoginContext context, final EventEmitter eventEmitter) {
        final int successCount = loginMetric.value(LoginResult.Status.SUCCESS.name());
        final int failureCount = loginMetric.value(LoginResult.Status.FAILURE.name());

        if (successCount + failureCount < minimumSampleSize || (successCount == 0 && failureCount == 0)) {
            return;
        }
        if (successCount > 0 && failureCount == 0) {
            eventEmitter.accept(new FailedLoginRatioDeactivationEvent(0));
            return;
        }
        if (failureCount > 0 && successCount == 0) {
            eventEmitter.accept(new FailedLoginRatioActivationEvent(1));
            return;
        }

        double ratio = ((double) failureCount / (failureCount + successCount));
        if (logger.isDebugEnabled()) {
            logger.debug("Calculated failed login ratio: " + ratio);
        }

        // ratio is 0-1 double, activationLevel is a percentage
        if (ratio * 100 >= activationLevel) {
            eventEmitter.accept(new FailedLoginRatioActivationEvent(ratio));
        }
        if (ratio * 100 < deactivationLevel) {
            eventEmitter.accept(new FailedLoginRatioDeactivationEvent(ratio));
        }

    }
}
