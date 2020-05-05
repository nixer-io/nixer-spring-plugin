package io.nixer.nixerplugin.core.detection.events;

import io.nixer.nixerplugin.core.fingerprint.loginThreshold.FingerprintFailedLoginOverThresholdEvent;

public interface EventVisitor {

    void accept(AnomalyEvent event);

    void accept(UserAgentFailedLoginOverThresholdEvent event);

    void accept(UsernameFailedLoginOverThresholdEvent event);

    void accept(IpFailedLoginOverThresholdEvent event);

    void accept(FingerprintFailedLoginOverThresholdEvent event);

    void accept(FailedLoginRatioEvent event);

    void accept(GlobalCredentialStuffingEvent event);
}
