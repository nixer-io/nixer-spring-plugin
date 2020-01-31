package io.nixer.nixerplugin.core.detection.rules.threshold;

import java.util.concurrent.atomic.AtomicInteger;

import io.nixer.nixerplugin.core.detection.rules.LoginRule;
import org.springframework.util.Assert;

abstract class ThresholdLoginRule implements LoginRule {

    private final AtomicInteger threshold;

    ThresholdLoginRule(final int threshold) {
        this.threshold = new AtomicInteger(threshold);
    }

    boolean isOverThreshold(int value) {
        return value > threshold.get();
    }

    //todo add actuator endpoint to read/update it
    public void setThreshold(final int threshold) {
        Assert.isTrue(threshold > 1, "Threshold must be greater than 1");
        this.threshold.set(threshold);
    }
}
