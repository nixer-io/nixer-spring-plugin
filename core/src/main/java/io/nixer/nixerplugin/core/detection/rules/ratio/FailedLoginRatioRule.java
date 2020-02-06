package io.nixer.nixerplugin.core.detection.rules.ratio;

import java.util.concurrent.atomic.AtomicInteger;

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

    private final Log logger = LogFactory.getLog(getClass());

    private final LoginMetric loginMetric;
    private final AtomicInteger activationLevel;
    private final AtomicInteger deactivationLevel;
    private final AtomicInteger minimumSampleSize;

    public FailedLoginRatioRule(final LoginMetric loginMetric, Integer activationLevel, Integer deactivationLevel, Integer minimumSampleSize) {
        Assert.notNull(loginMetric, "LoginMetric must not be null");
        this.loginMetric = loginMetric;

        Assert.notNull(activationLevel, "activationLevel must not be null");
        Assert.notNull(activationLevel, "deactivationLevel must not be null");
        if (activationLevel < deactivationLevel) {
            throw new IllegalStateException(String.format("Activation level (%d) must be equal or bigger than deactivation level (%d)",
                    activationLevel,
                    deactivationLevel));
        }
        this.activationLevel = new AtomicInteger(activationLevel);
        this.deactivationLevel = new AtomicInteger(deactivationLevel);

        Assert.notNull(minimumSampleSize, "minimumSampleSize must not be null");
        this.minimumSampleSize = new AtomicInteger(minimumSampleSize);
    }

    @Override
    public void execute(final LoginContext context, final EventEmitter eventEmitter) {
        final int successCount = loginMetric.value(LoginResult.Status.SUCCESS.getName());
        final int failureCount = loginMetric.value(LoginResult.Status.FAILURE.getName());
        final int minimumSample = minimumSampleSize.get();
        final int activation = activationLevel.get();
        final int deactivation = deactivationLevel.get();

        if (successCount + failureCount < minimumSample || (successCount == 0 && failureCount == 0)) {
            return;
        }
        if (successCount > 0 && failureCount == 0) {
            eventEmitter.accept(new FailedLoginRatioDeactivationEvent(0));
            return;
        }
        if (failureCount > 0 && successCount == 0) {
            eventEmitter.accept(new FailedLoginRatioActivationEvent(100));
            return;
        }

        double ratio = ((double) failureCount / (failureCount + successCount)) * 100;
        logger.debug("Calculated failed login ratio: " + ratio);

        if (ratio >= activation) {
            eventEmitter.accept(new FailedLoginRatioActivationEvent(ratio));
        }
        if (ratio < deactivation) {
            eventEmitter.accept(new FailedLoginRatioDeactivationEvent(ratio));
        }

    }
}
