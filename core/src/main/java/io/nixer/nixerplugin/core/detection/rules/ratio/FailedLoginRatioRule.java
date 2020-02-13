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

public class FailedLoginRatioRule implements LoginRule {

    private static final Log logger = LogFactory.getLog(FailedLoginRatioRule.class);

    private final LoginMetric loginMetric;
    private final int activationLevel;
    private final int deactivationLevel;
    private final int minimumSampleSize;

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
        final int successCount = loginMetric.value(LoginResult.Status.SUCCESS.getName());
        final int failureCount = loginMetric.value(LoginResult.Status.FAILURE.getName());

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

        if (ratio * 100 >= activationLevel) {
            eventEmitter.accept(new FailedLoginRatioActivationEvent(ratio));
        }
        if (ratio * 100 < deactivationLevel) {
            eventEmitter.accept(new FailedLoginRatioDeactivationEvent(ratio));
        }

    }
}
