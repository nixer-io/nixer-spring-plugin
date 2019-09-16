package eu.xword.nixer.nixerplugin.events;

public interface EventVisitor {

    void accept(BlockEvent event);

    void accept(LockUserEvent event);

    void accept(BlockSourceIpEvent event);

    void accept(GlobalCredentialStuffingEvent event);
}
