package io.nixer.nixerplugin.core.detection.rules.ratio;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.AtomicDouble;
import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioActivationEvent;
import io.nixer.nixerplugin.core.detection.events.FailedLoginRatioDeactivationEvent;
import io.nixer.nixerplugin.core.detection.rules.EventEmitter;
import io.nixer.nixerplugin.core.detection.rules.LoginRule;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;
import io.nixer.nixerplugin.core.login.inmemory.LoginMetric;
import org.springframework.util.Assert;

public class FailedLoginRatioRule implements LoginRule {

    private final LoginMetric loginMetric;
    private final AtomicDouble activationLevel;
    private final AtomicDouble deactivationLevel;
    private final AtomicInteger minimumSampleSize;

    public FailedLoginRatioRule(final LoginMetric loginMetric, double activationLevel, double deactivationLevel, int minimumSampleSize) {
        Assert.notNull(loginMetric, "LoginMetric must not be null");
        this.loginMetric = loginMetric;

        if (activationLevel <= deactivationLevel) {
            throw new IllegalStateException(String.format("Activation level (%f) must be equal or bigger than deactivation level (%f)",
                    activationLevel,
                    deactivationLevel));
        }
        this.activationLevel = new AtomicDouble(activationLevel);
        this.deactivationLevel = new AtomicDouble(deactivationLevel);

        this.minimumSampleSize = new AtomicInteger(minimumSampleSize);
    }

    @Override
    public void execute(final LoginContext context, final EventEmitter eventEmitter) {
        final int successCount = loginMetric.value(LoginResult.Status.SUCCESS.getName());
        final int failureCount = loginMetric.value(LoginResult.Status.FAILURE.getName());
        final int minimumSample = minimumSampleSize.get();
        final double activation = activationLevel.get();
        final double deactivation = deactivationLevel.get();

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

        double ratio = failureCount / (failureCount + successCount);

        if (ratio >= activation) {
            eventEmitter.accept(new FailedLoginRatioActivationEvent(ratio));
        }
        if (ratio < deactivation) {
            eventEmitter.accept(new FailedLoginRatioDeactivationEvent(ratio));
        }

    }
}
