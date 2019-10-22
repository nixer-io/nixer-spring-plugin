package eu.xword.nixer.nixerplugin.core.events;

public interface EventVisitor {

    void accept(AnomalyEvent event);

    void accept(UserAgentFailedLoginOverThresholdEvent event);

    void accept(UsernameFailedLoginOverThresholdEvent event);

    void accept(IpFailedLoginOverThresholdEvent event);

    void accept(GlobalCredentialStuffingEvent event);
}
