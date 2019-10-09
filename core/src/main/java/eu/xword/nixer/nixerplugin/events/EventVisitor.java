package eu.xword.nixer.nixerplugin.events;

public interface EventVisitor {

    void accept(BlockEvent event);

    void accept(LockUserEvent event);

    void accept(IpFailedLoginOverThresholdEvent event);

    void accept(GlobalCredentialStuffingEvent event);
}
