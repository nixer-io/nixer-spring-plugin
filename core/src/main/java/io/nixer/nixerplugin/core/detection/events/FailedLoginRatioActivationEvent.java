package io.nixer.nixerplugin.core.detection.events;

public class FailedLoginRatioActivationEvent extends FailedLoginRatioEvent {

    public FailedLoginRatioActivationEvent(double ratio) {
        super(ratio);
    }

    @Override
    public String type() {
        return FAILED_LOGIN_RATIO_ACTIVATION;
    }

}
