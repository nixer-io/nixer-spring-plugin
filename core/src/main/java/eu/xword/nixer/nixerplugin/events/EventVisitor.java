package eu.xword.nixer.nixerplugin.events;

public interface EventVisitor {

    void accept(DetectionEvent event);

    void accept(UsernameFailedLoginOverThresholdEvent event);

    void accept(IpFailedLoginOverThresholdEvent event);

    void accept(GlobalCredentialStuffingEvent event);
}
