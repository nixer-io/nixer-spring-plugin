package io.nixer.nixerplugin.core.detection.events;

public class FailedLoginRatioDeactivationEvent extends FailedLoginRatioEvent {

    public FailedLoginRatioDeactivationEvent(double ratio) {
        super(ratio);
    }

    @Override
    public String type() {
        return FAILED_LOGIN_RATIO_DEACTIVATION;
    }

}
